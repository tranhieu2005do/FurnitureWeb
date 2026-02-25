import { useState, useEffect, useRef } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import "./FloatingChat.css";
import chatService from "../../api/ChatService";

/* ── Helpers ─────────────────────────────────────────────────────── */
function formatTime(date) {
  if (!date) return "";
  const d = new Date(date);
  const now = new Date();
  const diff = now - d;
  if (diff < 60000) return "Vừa xong";
  if (diff < 3600000) return `${Math.floor(diff / 60000)} phút trước`;
  if (d.toDateString() === now.toDateString())
    return d.toLocaleTimeString("vi-VN", {
      hour: "2-digit",
      minute: "2-digit",
    });
  const yesterday = new Date(now);
  yesterday.setDate(yesterday.getDate() - 1);
  if (d.toDateString() === yesterday.toDateString())
    return (
      "Hôm qua " +
      d.toLocaleTimeString("vi-VN", { hour: "2-digit", minute: "2-digit" })
    );
  return (
    d.toLocaleDateString("vi-VN", { day: "2-digit", month: "2-digit" }) +
    " " +
    d.toLocaleTimeString("vi-VN", { hour: "2-digit", minute: "2-digit" })
  );
}

function formatFileSize(bytes) {
  if (bytes < 1024) return bytes + " B";
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + " KB";
  return (bytes / (1024 * 1024)).toFixed(1) + " MB";
}

function getFileIcon(type) {
  if (type.startsWith("image/")) return "🖼️";
  if (type.startsWith("video/")) return "🎬";
  if (type.startsWith("audio/")) return "🎵";
  if (type.includes("pdf")) return "📄";
  if (type.includes("word") || type.includes("document")) return "📝";
  if (type.includes("sheet") || type.includes("excel")) return "📊";
  if (type.includes("zip") || type.includes("rar")) return "🗜️";
  return "📎";
}

/* ── Message Bubble ──────────────────────────────────────────────── */
function MessageBubble({ msg, isMine }) {
  const [lightbox, setLightbox] = useState(null); // lưu media đang mở
    return (
      // text message
      (
        <div className={`fc-msg-bubble ${isMine ? "mine" : "theirs"}`}>
          {msg.message}
        </div>
      ) && (
        <>
          {msg.medias?.length > 0 && msg.medias.map((f, idx) => {
            if (f.type === "IMAGE") {
              <div key={idx}>
                <div
                  className={`fc-msg-bubble fc-msg-image ${
                    isMine ? "mine" : "theirs"
                  }`}
                >
                  <div
                    className="fc-image-wrapper"
                    onClick={() => setLightbox(f)}
                  >
                    <img src={f.url} alt={f.fileName} loading="lazy" />
                  </div>
                </div>

                {lightbox?.url === f.url && (
                  <div
                    className="fc-lightbox"
                    onClick={() => setLightbox(null)}
                  >
                    <img src={f.url} alt={f.fileName} />
                  </div>
                )}
              </div>;
            }

            if (f.type === "VIDEO") {
              <div
                key={idx}
                className={`fc-msg-bubble fc-msg-video ${
                  isMine ? "mine" : "theirs"
                }`}
              >
                <video controls preload="metadata">
                  <source src={f.url} />
                </video>
              </div>;
            }

            <a
              key={idx}
              className={`fc-msg-bubble fc-msg-file ${
                isMine ? "mine" : "theirs"
              }`}
              href={f.url}
              target="_blank"
              rel="noreferrer"
            >
              {f.fileName}
            </a>;
          })}
        </>
      )
    );
}

/* ── Preview Strip ───────────────────────────────────────────────── */
function FilePreview({ files, onRemove }) {
  if (!files.length) return null;
  return (
    <div className="fc-preview-strip">
      {files.map((f, i) => (
        <div key={i} className="fc-preview-item">
          {f.type.startsWith("image/") ? (
            <div className="fc-preview-thumb">
              <img src={f.dataUrl} alt={f.name} />
            </div>
          ) : f.type.startsWith("video/") ? (
            <div className="fc-preview-thumb video">
              <video src={f.dataUrl} />
              <span className="fc-preview-play-icon">▶</span>
            </div>
          ) : (
            <div className="fc-preview-thumb file">{getFileIcon(f.type)}</div>
          )}
          <div className="fc-preview-info">
            <span className="fc-preview-name">{f.name}</span>
            <span className="fc-preview-size">{formatFileSize(f.size)}</span>
          </div>
          <button className="fc-preview-remove" onClick={() => onRemove(i)}>
            ✕
          </button>
        </div>
      ))}
    </div>
  );
}

/* ── Main Component ──────────────────────────────────────────────── */
export default function FloatingChat({
  userId = 7,
  recipientId = 1,
  recipientName = "Hỗ trợ khách hàng",
  recipientAvatar,
  socketUrl = "http://localhost:8080/ws",
  position = "bottom-right",
  // Callback để upload file lên server, trả về URL
  // async (file) => { return 'https://cdn.example.com/file.jpg'; }
  onUploadFile = null,
}) {
  const [isOpen, setIsOpen] = useState(false);
  const [isMinimized, setMinimize] = useState(false);
  const [messages, setMessages] = useState([]);
  const [inputText, setInputText] = useState("");
  const [isTyping, setIsTyping] = useState(false);
  const [unreadCount, setUnread] = useState(0);
  const [isConnected, setConnected] = useState(false);
  const [pendingFiles, setPendingFiles] = useState([]); // files chờ gửi
  const [uploading, setUploading] = useState(false);
  const [showAttachMenu, setShowAttachMenu] = useState(false);
  const [conversationId, setConversationId] = useState(null);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [loading, setLoading] = useState(false);
  const [pageMessages, setPageMessages] = useState([]);

  const messagesContainerRef = useRef(null);
  const stompClientRef = useRef(null);
  const messagesEndRef = useRef(null);
  const inputRef = useRef(null);
  const typingTimeoutRef = useRef(null);
  const isOpenRef = useRef(isOpen);
  const isMinimizedRef = useRef(isMinimized);
  const fileInputRef = useRef(null);
  const imageInputRef = useRef(null);
  const videoInputRef = useRef(null);

  useEffect(() => {
    isOpenRef.current = isOpen;
  }, [isOpen]);
  useEffect(() => {
    isMinimizedRef.current = isMinimized;
  }, [isMinimized]);
  useEffect(() => {
    if (conversationId) {
      setPage(0);
      setHasMore(true);
      setMessages([]);
      loadMessages(0, conversationId);
    }
  }, [conversationId]);
  useEffect(() => {
    const fetchConversation = async () => {
      try {
        const response = await chatService.getConversation();
        //   console.log("conversation response", response);
        setConversationId(response.data);
      } catch (err) {
        console.error(err);
      }
    };

    fetchConversation();
  }, []);

  /* ── Notification sound ──────────────────────────────────────── */
  const playNotificationSound = () => {
    try {
      const audio = new Audio("/notification.mp3");
      audio.volume = 0.3;
      audio.play().catch(() => {});
    } catch (err) {}
  };

  /* ── STOMP connection ────────────────────────────────────────── */
  useEffect(() => {
    if (!userId) return;

    const client = new Client({
      webSocketFactory: () => new SockJS(socketUrl),
      debug: (str) => {
        if (import.meta.env.DEV) console.log("[STOMP]", str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,

      onConnect: () => {
        setConnected(true);

        client.subscribe(`/topic/messages/${userId}`, (message) => {
          try {
            const msg = JSON.parse(message.body);
            setMessages((prev) => [...prev, msg]);
            if (!isOpenRef.current || isMinimizedRef.current) {
              setUnread((prev) => prev + 1);
              playNotificationSound();
            }
          } catch (err) {
            console.error(err);
          }
        });

        client.subscribe(`/topic/typing/${userId}`, (message) => {
          const data = JSON.parse(message.body);
          if (data.senderId === recipientId) setIsTyping(true);
        });

        client.subscribe(`/topic/stop-typing/${userId}`, (message) => {
          const data = JSON.parse(message.body);
          if (data.senderId === recipientId) setIsTyping(false);
        });

        client.subscribe(`/topic/history/${userId}`, (message) => {
          setMessages(JSON.parse(message.body) ?? []);
        });

        client.publish({
          destination: "/app/chat.history",
          body: JSON.stringify({ userId, recipientId }),
        });
      },

      onDisconnect: () => setConnected(false),
      onStompError: () => setConnected(false),
    });

    client.activate();
    stompClientRef.current = client;
    return () => client.deactivate();
  }, [userId, recipientId, socketUrl]);

  /* ── Auto scroll ─────────────────────────────────────────────── */
  useEffect(() => {
    if (isOpen && !isMinimized)
      messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, isOpen, isMinimized]);

  useEffect(() => {
    const container = messagesContainerRef.current;
    if (!container) return;

    const handleScroll = () => {
      // Khi cuộn đến gần đỉnh (< 50px) thì load thêm
      if (container.scrollTop < 50 && hasMore && !loading) {
        loadMessages();
      }
    };

    container.addEventListener("scroll", handleScroll);
    return () => container.removeEventListener("scroll", handleScroll);
  }, [hasMore, loading, page, conversationId]);

  /* ── Open/close ──────────────────────────────────────────────── */
  const handleToggle = () => {
    if (isOpen) {
      setMinimize((prev) => !prev);
    } else {
      setIsOpen(true);
      setMinimize(false);
      setUnread(0);
      setTimeout(() => inputRef.current?.focus(), 100);
    }
  };
  const handleClose = () => {
    setIsOpen(false);
    setMinimize(false);
  };

  /* ── Typing indicator ────────────────────────────────────────── */
  const handleTyping = () => {
    const client = stompClientRef.current;
    if (!client?.connected) return;
    client.publish({
      destination: "/app/chat.typing",
      body: JSON.stringify({ senderId: userId, recipientId }),
    });
    if (typingTimeoutRef.current) clearTimeout(typingTimeoutRef.current);
    typingTimeoutRef.current = setTimeout(() => {
      client.publish({
        destination: "/app/chat.stop-typing",
        body: JSON.stringify({ senderId: userId, recipientId }),
      });
    }, 2000);
  };

  /* ── File picker ─────────────────────────────────────────────── */
  const readFileAsDataUrl = (file) =>
    new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = (e) => resolve(e.target.result);
      reader.onerror = reject;
      reader.readAsDataURL(file);
    });

  const handleFilesSelected = async (e) => {
    const files = Array.from(e.target.files);
    if (!files.length) return;
    setShowAttachMenu(false);

    const MAX_SIZE = 50 * 1024 * 1024; // 50 MB
    const valid = files.filter((f) => {
      if (f.size > MAX_SIZE) {
        alert(`File "${f.name}" vượt quá 50MB`);
        return false;
      }
      return true;
    });

    const previews = await Promise.all(
      valid.map(async (f) => ({
        file: f,
        name: f.name,
        type: f.type,
        size: f.size,
        dataUrl: await readFileAsDataUrl(f),
      }))
    );

    setPendingFiles((prev) => [...prev, ...previews]);
    e.target.value = "";
  };

  const removePendingFile = (idx) =>
    setPendingFiles((prev) => prev.filter((_, i) => i !== idx));

  /* ── Send message / files ────────────────────────────────────── */
  const publishMessage = (msg) => {
    const client = stompClientRef.current;
    if (!client?.connected) return;
    client.publish({
      destination: "/app/chat.send",
      body: JSON.stringify(msg),
    });
  };

  const sendMessage = async () => {
    const client = stompClientRef.current;
    if (!client?.connected) return;
    if (!inputText.trim() && !pendingFiles.length) return;

    setUploading(true);

    try {
      const formData = new FormData();
      // Gửi từng file
      formData.append("senderId", userId);
      formData.append("conservationId", conversationId);
      if (pendingFiles.length > 0) {
        pendingFiles.forEach((pf) => {
          formData.append("files", pf.file);
        });
      }
      for (const pf of pendingFiles) {
        let fileUrl = null;

        if (typeof onUploadFile === "function") {
          // Upload lên server → lấy URL
          fileUrl = await onUploadFile(pf.file);
        }

        const fileType = pf.type.startsWith("image/")
          ? "image"
          : pf.type.startsWith("video/")
          ? "video"
          : "file";

        const msg = {
          senderId: userId,
          recipientId,
          type: fileType,
          content: pf.name,
          fileName: pf.name,
          fileSize: pf.size,
          fileType: pf.type,
          // Nếu có URL thì dùng URL, không thì gửi base64 (chỉ nên dùng cho ảnh nhỏ)
          fileUrl: fileUrl || null,
          fileData: fileUrl ? null : pf.dataUrl,
          timestamp: new Date().toISOString(),
        };

        publishMessage(msg);
        setMessages((prev) => [
          ...prev,
          { ...msg, id: Date.now() + Math.random(), senderId: userId },
        ]);
      }

      // Gửi text nếu có
      if (inputText.trim()) {
        const msg = {
          senderId: userId,
          recipientId,
          type: "text",
          content: inputText.trim(),
          timestamp: new Date().toISOString(),
        };
        formData.append("content", msg.content);
        publishMessage(msg);
        setMessages((prev) => [
          ...prev,
          { ...msg, id: Date.now(), senderId: userId },
        ]);
      }
      console.log("formdata", [...formData.entries()]);
      const response = chatService.sendMessage(formData);
      console.log("send message:", response.data);
    } catch (err) {
      console.error("Send error:", err);
      alert("Gửi thất bại, vui lòng thử lại.");
    } finally {
      setUploading(false);
      setPendingFiles([]);
      setInputText("");
      client.publish({
        destination: "/app/chat.stop-typing",
        body: JSON.stringify({ senderId: userId, recipientId }),
      });
      if (typingTimeoutRef.current) clearTimeout(typingTimeoutRef.current);
    }
  };

  const loadMessages = async (pageOverride, conversationIdOverride) => {
    const currentPage = pageOverride ?? page;
    const currentConversationId = conversationIdOverride ?? conversationId;

    if (!currentConversationId) return;

    // Dùng ref để tránh stale closure khi gọi từ scroll handler
    if (loading) return;
    if (pageOverride === undefined && !hasMore) return;

    setLoading(true);

    try {
      const container = messagesContainerRef.current;
      const prevScrollHeight = container ? container.scrollHeight : 0;

      const res = await chatService.getMessageConservationByUserId({
        params: {
          conservationId: currentConversationId,
          page: currentPage,
          size: 20,
        },
      });

      console.log(res.data.content);

      const newMessages = res.data.content;
      const isLast = res.data.last;

      setMessages((prev) => [...newMessages, ...prev]);
      setHasMore(!isLast);
      setPage(currentPage + 1);

      // Giữ nguyên vị trí scroll sau khi prepend tin nhắn cũ
      if (container && currentPage > 0) {
        requestAnimationFrame(() => {
          container.scrollTop = container.scrollHeight - prevScrollHeight;
        });
      }
    } catch (err) {
      console.error("Load messages error:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      sendMessage();
    }
  };

  /* ── Drag & Drop ─────────────────────────────────────────────── */
  const handleDrop = (e) => {
    e.preventDefault();
    const files = Array.from(e.dataTransfer.files);
    if (files.length) handleFilesSelected({ target: { files, value: "" } });
  };

  /* ── Render ──────────────────────────────────────────────────── */
  if (!userId) return null;
  const posClass = position === "bottom-left" ? "fc-pos-left" : "fc-pos-right";
  const canSend =
    (inputText.trim() || pendingFiles.length) && isConnected && !uploading;

  return (
    <>
      {/* Hidden file inputs */}
      <input
        ref={imageInputRef}
        type="file"
        accept="image/*"
        multiple
        hidden
        onChange={handleFilesSelected}
      />
      <input
        ref={videoInputRef}
        type="file"
        accept="video/*"
        multiple
        hidden
        onChange={handleFilesSelected}
      />
      <input
        ref={fileInputRef}
        type="file"
        multiple
        hidden
        onChange={handleFilesSelected}
      />

      {/* ══ Floating Button ══ */}
      {!isOpen && (
        <button
          className={`fc-float-btn ${posClass} ${
            unreadCount > 0 ? "has-unread" : ""
          }`}
          onClick={handleToggle}
          title="Chat với chúng tôi"
        >
          {recipientAvatar ? (
            <img src={recipientAvatar} alt="Chat" className="fc-avatar-img" />
          ) : (
            <svg
              width="28"
              height="28"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
            >
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
            </svg>
          )}
          {unreadCount > 0 && (
            <span className="fc-unread-badge">
              {unreadCount > 99 ? "99+" : unreadCount}
            </span>
          )}
          {isConnected && <span className="fc-online-dot" />}
        </button>
      )}

      {/* ══ Chat Window ══ */}
      {isOpen && (
        <div
          className={`fc-window ${posClass} ${isMinimized ? "minimized" : ""}`}
          onDragOver={(e) => e.preventDefault()}
          onDrop={handleDrop}
        >
          {/* Header */}
          <div className="fc-header">
            <div className="fc-header-left">
              <div className="fc-avatar">
                {recipientAvatar ? (
                  <img src={recipientAvatar} alt={recipientName} />
                ) : (
                  <span>{recipientName[0]}</span>
                )}
                {isConnected && <span className="fc-avatar-status" />}
              </div>
              <div className="fc-header-info">
                <span className="fc-recipient-name">{recipientName}</span>
                {isTyping ? (
                  <span className="fc-typing-indicator">Đang soạn tin...</span>
                ) : (
                  <span className="fc-status">
                    {isConnected ? "Đang hoạt động" : "Không trực tuyến"}
                  </span>
                )}
              </div>
            </div>
            <div className="fc-header-actions">
              <button
                className="fc-icon-btn"
                onClick={() => setMinimize(!isMinimized)}
                title={isMinimized ? "Mở rộng" : "Thu nhỏ"}
              >
                <svg
                  width="18"
                  height="18"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="2"
                >
                  {isMinimized ? (
                    <polyline points="18 15 12 9 6 15" />
                  ) : (
                    <line x1="5" y1="12" x2="19" y2="12" />
                  )}
                </svg>
              </button>
              <button
                className="fc-icon-btn"
                onClick={handleClose}
                title="Đóng"
              >
                <svg
                  width="18"
                  height="18"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="2"
                >
                  <line x1="18" y1="6" x2="6" y2="18" />
                  <line x1="6" y1="6" x2="18" y2="18" />
                </svg>
              </button>
            </div>
          </div>

          {/* Messages */}
          {!isMinimized && (
            <>
              <div className="fc-messages" ref={messagesContainerRef}>
                {messages.length === 0 ? (
                  <div className="fc-empty">
                    <span className="fc-empty-icon">💬</span>
                    <p>Chưa có tin nhắn nào</p>
                    <p className="fc-empty-hint">
                      Bắt đầu cuộc trò chuyện với chúng tôi!
                    </p>
                  </div>
                ) : (
                  <>
                    {/* Loading indicator ở đầu danh sách */}
                    {loading && (
                      <div className="fc-load-more-indicator">
                        <span className="fc-spinner" /> Đang tải...
                      </div>
                    )}
                    {!hasMore && messages.length > 0 && (
                      <div className="fc-no-more">Đã tải tất cả tin nhắn</div>
                    )}
                    {messages.map((msg, idx) => {
                      const isMine = msg.senderId === userId;
                      const prevMsg = messages[idx - 1];
                      const prevSender = prevMsg?.senderId;
                      const showAvatar = !isMine && prevSender !== msg.senderId;

                      return (
                        <div
                          key={msg.id}
                          className={`fc-msg ${isMine ? "mine" : "theirs"}`}
                        >
                          {showAvatar && (
                            <div className="fc-msg-avatar">
                              {recipientAvatar ? (
                                <img src={recipientAvatar} alt="" />
                              ) : (
                                <span>{recipientName[0]}</span>
                              )}
                            </div>
                          )}
                          <div className="fc-msg-content">
                            <MessageBubble msg={msg} isMine={isMine} />
                            <span className="fc-msg-time">
                              {formatTime(msg.sentAt)}
                            </span>
                          </div>
                        </div>
                      );
                    })}

                    {isTyping && (
                      <div className="fc-msg theirs">
                        <div className="fc-msg-avatar">
                          {recipientAvatar ? (
                            <img src={recipientAvatar} alt="" />
                          ) : (
                            <span>{recipientName[0]}</span>
                          )}
                        </div>
                        <div className="fc-typing-bubble">
                          <span />
                          <span />
                          <span />
                        </div>
                      </div>
                    )}
                    <div ref={messagesEndRef} />
                  </>
                )}
              </div>

              {/* File previews */}
              <FilePreview files={pendingFiles} onRemove={removePendingFile} />

              {/* Input area */}
              <div className="fc-input-wrap">
                {/* Attach button + dropdown */}
                <div className="fc-attach-wrap">
                  <button
                    className={`fc-icon-btn fc-attach-btn ${
                      showAttachMenu ? "active" : ""
                    }`}
                    onClick={() => setShowAttachMenu((prev) => !prev)}
                    disabled={!isConnected || uploading}
                    title="Đính kèm"
                  >
                    📎
                  </button>

                  {showAttachMenu && (
                    <div className="fc-attach-menu">
                      <button
                        onClick={() => {
                          imageInputRef.current.click();
                          setShowAttachMenu(false);
                        }}
                      >
                        <span>🖼️</span> Ảnh
                      </button>
                      <button
                        onClick={() => {
                          videoInputRef.current.click();
                          setShowAttachMenu(false);
                        }}
                      >
                        <span>🎬</span> Video
                      </button>
                      <button
                        onClick={() => {
                          fileInputRef.current.click();
                          setShowAttachMenu(false);
                        }}
                      >
                        <span>📎</span> File
                      </button>
                    </div>
                  )}
                </div>

                <textarea
                  ref={inputRef}
                  className="fc-input"
                  placeholder={uploading ? "Đang gửi..." : "Aa"}
                  value={inputText}
                  onChange={(e) => {
                    setInputText(e.target.value);
                    handleTyping();
                  }}
                  onKeyDown={handleKeyDown}
                  rows={1}
                  disabled={!isConnected || uploading}
                />

                <button
                  className="fc-send-btn"
                  onClick={sendMessage}
                  disabled={!canSend}
                >
                  {uploading ? (
                    <span className="fc-spinner" />
                  ) : (
                    <svg
                      width="20"
                      height="20"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      strokeWidth="2"
                    >
                      <line x1="22" y1="2" x2="11" y2="13" />
                      <polygon points="22 2 15 22 11 13 2 9 22 2" />
                    </svg>
                  )}
                </button>
              </div>
            </>
          )}
        </div>
      )}
    </>
  );
}
