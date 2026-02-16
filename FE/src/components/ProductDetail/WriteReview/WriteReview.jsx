import { useState, useRef } from "react";
import commentService from "../../../api/CommentService";
import "./WriteReview.css";

// /* ── Star Picker ─────────────────────────────────────────────────── */
// function StarPicker({ value, onChange }) {
//   const [hovered, setHovered] = useState(0);
//   const LABELS = ['', 'Rất tệ', 'Tệ', 'Bình thường', 'Tốt', 'Xuất sắc'];
//   const active = hovered || value;

//   return (
//     <div className="wr-star-group">
//       <span className="wr-star-picker">
//         {[1, 2, 3, 4, 5].map(i => (
//           <button
//             key={i}
//             type="button"
//             className={`wr-star-btn ${i <= active ? 'lit' : ''}`}
//             onMouseEnter={() => setHovered(i)}
//             onMouseLeave={() => setHovered(0)}
//             onClick={() => onChange(i)}
//           >★</button>
//         ))}
//       </span>
//       <span className={`wr-star-label ${active ? 'show' : ''}`}>
//         {LABELS[active]}
//       </span>
//     </div>
//   );
// }

/* ── Main Component ──────────────────────────────────────────────── */
export default function WriteReview({ productId, onSubmitSuccess }) {
  const [comment, setComment] = useState("");
  const [mediaFiles, setMediaFiles] = useState([]);
  const [submitStatus, setSubmitStatus] = useState("idle"); // idle | loading | success | error

  const imgInputRef = useRef(null);
  const videoInputRef = useRef(null);

  /* ── Helpers ─────────────────────────────────────────────────── */
  const addFiles = (fileList) => {
    const incoming = Array.from(fileList);
    setMediaFiles((prev) => {
      const names = new Set(prev.map((f) => f.name));
      return [...prev, ...incoming.filter((f) => !names.has(f.name))];
    });
  };

  /* ── Submit ──────────────────────────────────────────────────── */
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!comment.trim() || submitStatus === "loading") return;
    try {
      setSubmitStatus("loading");
      const formData = new FormData();
      formData.append("productId", productId);
      formData.append("content", comment);

      mediaFiles.forEach((file) => {
        formData.append("files", file);
      });

      const response = await commentService.sendComment(formData);
      console.log("Send comment response:", response);
      setSubmitStatus("success");
      setComment("");
      setMediaFiles([]);
      onSubmitSuccess?.();
    } catch {
      setSubmitStatus("error");
    } finally {
      setTimeout(() => setSubmitStatus("idle"), 3000);
    }
  };

  const charPct = (comment.length / 1000) * 100;
  const isDisabled = !comment.trim() || submitStatus === "loading";

  return (
    <div className="wr-root">
      {/* ── Header ─────────────────────────────────────────────── */}
      <div className="wr-header">
        <div>
          <h3 className="wr-title">Viết cảm nhận</h3>
          <p className="wr-subtitle">Đánh giá giúp ích cho cộng đồng mua sắm</p>
        </div>
        {/* <StarPicker value={star} onChange={setStar} /> */}
      </div>

      {/* ── Form ───────────────────────────────────────────────── */}
      <form className="wr-form" onSubmit={handleSubmit}>
        {/* Textarea box — toolbar nằm trong khung */}
        <div className="wr-box">
          <textarea
            className="wr-textarea"
            placeholder="Chia sẻ trải nghiệm về chất lượng, kích thước, giao hàng..."
            value={comment}
            onChange={(e) => setComment(e.target.value.slice(0, 1000))}
            rows={5}
          />

          {/* Progress bar mỏng dưới textarea */}
          <div className="wr-progress">
            <div
              className={`wr-progress-fill ${charPct > 90 ? "warn" : ""}`}
              style={{ width: `${charPct}%` }}
            />
          </div>

          {/* Toolbar */}
          <div className="wr-toolbar">
            <div className="wr-toolbar-actions">
              {/* Icon ảnh */}
              <button
                type="button"
                className="wr-tb-btn"
                title="Đính kèm hình ảnh"
                onClick={() => imgInputRef.current?.click()}
              >
                <svg
                  width="18"
                  height="18"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="1.8"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                >
                  <rect x="3" y="3" width="18" height="18" rx="3" />
                  <circle cx="8.5" cy="8.5" r="1.5" />
                  <polyline points="21 15 16 10 5 21" />
                </svg>
              </button>

              {/* Icon video */}
              <button
                type="button"
                className="wr-tb-btn"
                title="Đính kèm video"
                onClick={() => videoInputRef.current?.click()}
              >
                <svg
                  width="18"
                  height="18"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="1.8"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                >
                  <rect x="2" y="6" width="14" height="12" rx="2.5" />
                  <polygon points="22 8 16 12 22 16 22 8" />
                </svg>
              </button>

              {/* Badge số file */}
              {mediaFiles.length > 0 && (
                <span className="wr-file-badge">{mediaFiles.length}</span>
              )}
            </div>

            {/* Char count bên phải */}
            <span className={`wr-char ${comment.length > 900 ? "warn" : ""}`}>
              {comment.length}/1000
            </span>
          </div>
        </div>

        {/* Hidden inputs */}
        <input
          ref={imgInputRef}
          type="file"
          multiple
          accept="image/*"
          style={{ display: "none" }}
          onChange={(e) => {
            addFiles(e.target.files);
            e.target.value = "";
          }}
        />
        <input
          ref={videoInputRef}
          type="file"
          multiple
          accept="video/*"
          style={{ display: "none" }}
          onChange={(e) => {
            addFiles(e.target.files);
            e.target.value = "";
          }}
        />

        {/* ── Media preview ──────────────────────────────────── */}
        {mediaFiles.length > 0 && (
          <div className="wr-media">
            {mediaFiles.map((file, i) => {
              const url = URL.createObjectURL(file);
              const isImg = file.type.startsWith("image");
              return (
                <div key={i} className="wr-media-cell">
                  {isImg ? (
                    <img src={url} alt="preview" />
                  ) : (
                    <div className="wr-video-wrap">
                      <video src={url} />
                      <span className="wr-play-icon">
                        <svg
                          width="16"
                          height="16"
                          viewBox="0 0 24 24"
                          fill="white"
                        >
                          <polygon points="5 3 19 12 5 21 5 3" />
                        </svg>
                      </span>
                    </div>
                  )}
                  <button
                    type="button"
                    className="wr-remove"
                    onClick={() =>
                      setMediaFiles((p) => p.filter((_, j) => j !== i))
                    }
                  >
                    <svg
                      width="8"
                      height="8"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="white"
                      strokeWidth="3.5"
                    >
                      <line x1="18" y1="6" x2="6" y2="18" />
                      <line x1="6" y1="6" x2="18" y2="18" />
                    </svg>
                  </button>
                  <span className="wr-media-tag">{isImg ? "🖼" : "🎬"}</span>
                </div>
              );
            })}

            {/* Ô thêm */}
            <button
              type="button"
              className="wr-media-add"
              onClick={() => imgInputRef.current?.click()}
            >
              <svg
                width="20"
                height="20"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="1.6"
              >
                <line x1="12" y1="5" x2="12" y2="19" />
                <line x1="5" y1="12" x2="19" y2="12" />
              </svg>
            </button>
          </div>
        )}

        {/* ── Footer ─────────────────────────────────────────── */}
        <div className="wr-footer">
          <span className="wr-attach-info">
            {mediaFiles.length > 0 && `${mediaFiles.length} tệp đính kèm`}
          </span>

          <div className="wr-footer-right">
            {submitStatus === "success" && (
              <span className="wr-status success">✓ Đã gửi thành công!</span>
            )}
            {submitStatus === "error" && (
              <span className="wr-status error">✗ Gửi thất bại</span>
            )}

            <button
              type="submit"
              className={`wr-submit ${submitStatus}`}
              disabled={isDisabled}
            >
              {submitStatus === "loading" ? (
                <>
                  <span className="wr-spin" /> Đang gửi...
                </>
              ) : (
                <>
                  <svg
                    width="15"
                    height="15"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2.2"
                  >
                    <line x1="22" y1="2" x2="11" y2="13" />
                    <polygon points="22 2 15 22 11 13 2 9 22 2" />
                  </svg>
                  Gửi bình luận
                </>
              )}
            </button>
          </div>
        </div>
      </form>
    </div>
  );
}
