import React, { useState, useEffect } from 'react';
// import { productService } from '../api';
import './ProductListPage.css';
import NavBar from '../Navbar/NavBar';
import productService from '../../api/ProductService';
import categoryService from '../../api/CategoriesService';
import ratingService from '../../api/RatingService';
import ProductCard from './ProductCard/ProductCard';

export default function ProductListPage() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [viewMode, setViewMode] = useState('grid'); // 'grid' or 'list'
  const [sortBy, setSortBy] = useState('newest');
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [categories, setCategories] = useState([]);
  
  // Filter states
  const [filters, setFilters] = useState({
    category: 0,
    priceRange: [0, 100000000],
    materials: [],
    colors: [],
    inStock: false,
  });

  // const [filter, setFilter] = useState({
  //   page: 0,
  //   size: 8,
  //   minPrice: "",
  //   maxPrice: "",
  //   categoryId: "",
  //   star: "",
  //   inStock: ""
  // });

  const [showMobileFilter, setShowMobileFilter] = useState(false);

  // Mock data - thay thế bằng API call thực tế
  useEffect(() => {
    const fetchProducts = async () => {
      try {
        setLoading(true);
        const res = await productService.getProducts({
          page: 0,
          size: 10,
          // min_price: 1000000,
          // max_price: 5000000,
          in_stock: true,
        });

        console.log("Products Response: ", res.data.content);
        // Mock data
        setTimeout(() => {
          setProducts(mockProducts);
          setTotalPages(3);
          setLoading(false);
        }, 500);
      } catch (error) {
        console.error('Error fetching products:', error);
        setLoading(false);
      }
    };
    fetchProducts();
  }, [currentPage, sortBy, filters]);

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await categoryService.getCategories();
        console.log("Response:", response);
        setCategories(response.data);
      } catch (error) {
        console.error("Error fetching categories:", error);
      }
    };

    fetchCategories();
  }, []);



  const STAR_OPTIONS = [
    { value: 4.5, label: '4.5 ⭐ trở lên' },
    { value: 4.0, label: '4.0 ⭐ trở lên' },
    { value: 3.5, label: '3.5 ⭐ trở lên' },
    { value: 3.0, label: '3.0 ⭐ trở lên' },
  ];

  const materials = [
    { label: 'Gỗ Sồi', value: 'GO_SOI' },
    { label: 'Gỗ Tần Bì', value: 'GO_TAN_BI' },
    { label: 'Gỗ Óc Chó', value: 'GO_OC_CHO' },
    { label: 'Inox', value: 'INOX' },
    { label: 'Nhôm', value: 'NHOM' },
    { label: 'Marble', value: 'MARBLE' },
    { label: 'Granite', value: 'GRANITE' },
    { label: 'Vải', value: 'VAI' },
    { label: 'Kính', value: 'KINH'}
  ];
  const colors = [
    { label: 'Be', value: 'BE' },
    { label: 'Trắng', value: 'TRANG' },
    { label: 'Đen', value: 'DEN' },
    { label: 'Xám Nhạt', value: 'XAM_NHAT' },
    { label: 'Xám Đậm', value: 'XAM_DAM' },
    { label: 'Nâu Gỗ Nhạt', value: 'NAU_GO_NHAT' },
    { label: 'Nâu Gỗ Đậm', value: 'NAU_GO_DAM' }
  ];
  const priceRanges = [
    { label: 'Dưới 5 triệu', value: [0, 5000000] },
    { label: '5 - 10 triệu', value: [5000000, 10000000] },
    { label: '10 - 20 triệu', value: [10000000, 20000000] },
    { label: '20 - 50 triệu', value: [20000000, 50000000] },
    { label: 'Trên 50 triệu', value: [50000000, 100000000] },
  ];

  const handleFilterChange = (filterType, value) => {
    setFilters(prev => ({
      ...prev,
      [filterType]: value
    }));
    setCurrentPage(1);
  };

  const handleArrayFilterToggle = (filterType, value) => {
    setFilters(prev => ({
      ...prev,
      [filterType]: prev[filterType].includes(value)
        ? prev[filterType].filter(item => item !== value)
        : [...prev[filterType], value]
    }));
    setCurrentPage(1);
  };

  const clearFilters = () => {
    setFilters({
      category: 0,
      priceRange: [0, 100000000],
      materials: [],
      colors: [],
      inStock: false,
    });
  };

  return (
    <div className="product-list-page">
      <NavBar />

      {/* Page Header */}
      <div className="page-header">
        <div className="header-content">
          <div className="header-text">
            <h1 className="page-title">Bộ Sưu Tập Nội Thất</h1>
            <p className="page-description">
              Khám phá hơn 500+ sản phẩm nội thất cao cấp cho ngôi nhà của bạn
            </p>
          </div>
          <div className="header-stats">
            <span className="result-count">Hiển thị {products.length} / 156 sản phẩm</span>
          </div>
        </div>
      </div>

      <div className="main-content">
        {/* Sidebar Filters */}
        <aside className={`filter-sidebar ${showMobileFilter ? 'mobile-show' : ''}`}>
          <div className="filter-header">
            <h3>Bộ Lọc</h3>
            <button className="clear-filters" onClick={clearFilters}>
              Xóa tất cả
            </button>
            <button 
              className="close-mobile-filter"
              onClick={() => setShowMobileFilter(false)}
            >
              ✕
            </button>
          </div>

          {/* Categories Filter */}
          <div className="filter-section">
            <h4 className="filter-title">Danh Mục</h4>
            <div className="filter-options">
              {categories.map(cat => (
                <label key={cat.id} className="filter-option">
                  <input
                    type="radio"
                    name="category"
                    checked={filters.category === cat.id}
                    onChange={() => handleFilterChange('category', cat.id)}
                  />
                  <span className="option-label">
                    {cat.name}
                  </span>
                </label>
              ))}
            </div>
          </div>

          {/* Price Range Filter */}
          <div className="filter-section">
            <h4 className="filter-title">Khoảng Giá</h4>
            <div className="filter-options">
              {priceRanges.map((range, index) => (
                <label key={index} className="filter-option">
                  <input
                    type="radio"
                    name="priceRange"
                    checked={JSON.stringify(filters.priceRange) === JSON.stringify(range.value)}
                    onChange={() => handleFilterChange('priceRange', range.value)}
                  />
                  <span className="option-label">{range.label}</span>
                </label>
              ))}
            </div>
          </div>

          {/* Material Filter */}
          <div className="filter-section">
            <h4 className="filter-title">Chất Liệu</h4>
            <div className="filter-options">
              {materials.map((material) => (
                <label key={material.value} className="filter-option">
                  <input
                    type="checkbox"
                    checked={filters.materials.includes(material.value)}
                    onChange={() =>
                      handleArrayFilterToggle('materials', material.value)
                    }
                  />
                  <span className="option-label">{material.label}</span>
                </label>
              ))}
            </div>
          </div>

          {/* Color Filter */}
          <div className="filter-section">
            <h4 className="filter-title">Màu Sắc</h4>
            <div className="color-options">
              {colors.map(color => (
                <button
                  key={color.value}
                  className={`color-option ${
                    filters.colors.includes(color.value) ? 'active' : ''
                  }`}
                  onClick={() => handleArrayFilterToggle('colors', color.value)}
                  title={color.label}
                >
                  <span
                    className="color-circle"
                    style={{ background: getColorCode(color.value) }}
                  ></span>
                </button>
              ))}
            </div>
          </div>

          {/* ── Đánh giá sao → star (GET /products) ── */}
          <div className="filter-section">
            <h4 className="filter-title">Đánh Giá</h4>
            <div className="filter-options">
              <label className="filter-option">
                <input
                  type="radio"
                  name="star"
                  checked={filters.star === null}
                  onChange={() => handleFilterChange('star', null)}
                />
                <span className="option-label">Tất cả</span>
              </label>
              {STAR_OPTIONS.map(opt => (
                <label key={opt.value} className="filter-option">
                  <input
                    type="radio"
                    name="star"
                    checked={filters.star === opt.value}
                    onChange={() => handleFilterChange('star', opt.value)}
                  />
                  <span className="option-label">{opt.label}</span>
                </label>
              ))}
            </div>
          </div>

          {/* Availability Filter */}
          <div className="filter-section">
            <label className="filter-option">
              <input
                type="checkbox"
                checked={filters.inStock}
                onChange={(e) => handleFilterChange('inStock', e.target.checked)}
              />
              <span className="option-label">Chỉ hiển thị hàng còn trong kho</span>
            </label>
          </div>
        </aside>

        {/* Products Area */}
        <div className="products-area">
          {/* Toolbar */}
          <div className="products-toolbar">
            <button 
              className="mobile-filter-btn"
              onClick={() => setShowMobileFilter(true)}
            >
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <line x1="4" y1="6" x2="20" y2="6"/>
                <line x1="4" y1="12" x2="20" y2="12"/>
                <line x1="4" y1="18" x2="20" y2="18"/>
              </svg>
              Bộ lọc
            </button>

            <div className="view-controls">
              <button 
                className={`view-btn ${viewMode === 'grid' ? 'active' : ''}`}
                onClick={() => setViewMode('grid')}
                title="Lưới"
              >
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <rect x="3" y="3" width="7" height="7"/>
                  <rect x="14" y="3" width="7" height="7"/>
                  <rect x="3" y="14" width="7" height="7"/>
                  <rect x="14" y="14" width="7" height="7"/>
                </svg>
              </button>
              <button 
                className={`view-btn ${viewMode === 'list' ? 'active' : ''}`}
                onClick={() => setViewMode('list')}
                title="Danh sách"
              >
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <line x1="8" y1="6" x2="21" y2="6"/>
                  <line x1="8" y1="12" x2="21" y2="12"/>
                  <line x1="8" y1="18" x2="21" y2="18"/>
                  <line x1="3" y1="6" x2="3.01" y2="6"/>
                  <line x1="3" y1="12" x2="3.01" y2="12"/>
                  <line x1="3" y1="18" x2="3.01" y2="18"/>
                </svg>
              </button>
            </div>

            <div className="sort-control">
              <label>Sắp xếp:</label>
              <select value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
                <option value="newest">Mới nhất</option>
                <option value="price_asc">Giá: Thấp đến cao</option>
                <option value="price_desc">Giá: Cao đến thấp</option>
                <option value="popular">Phổ biến nhất</option>
                <option value="rating">Đánh giá cao</option>
              </select>
            </div>
          </div>

          {/* Products Grid/List */}
          {loading ? (
            <div className="loading-state">
              <div className="spinner"></div>
              <p>Đang tải sản phẩm...</p>
            </div>
          ) : (
            <>
              <div className={`products-container ${viewMode}`}>
                {products.map((product, index) => (
                  <ProductCard 
                    key={product.id} 
                    product={product} 
                    viewMode={viewMode}
                    animationDelay={index * 0.05}
                  />
                ))}
              </div>

              {/* Pagination */}
              <div className="pagination">
                <button 
                  className="page-btn"
                  disabled={currentPage === 1}
                  onClick={() => setCurrentPage(prev => prev - 1)}
                >
                  ← Trước
                </button>
                
                <div className="page-numbers">
                  {[...Array(totalPages)].map((_, i) => (
                    <button
                      key={i + 1}
                      className={`page-number ${currentPage === i + 1 ? 'active' : ''}`}
                      onClick={() => setCurrentPage(i + 1)}
                    >
                      {i + 1}
                    </button>
                  ))}
                </div>

                <button 
                  className="page-btn"
                  disabled={currentPage === totalPages}
                  onClick={() => setCurrentPage(prev => prev + 1)}
                >
                  Sau →
                </button>
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );
}

// Helper function
const getColorCode = (value) => {
  const colorMap = {
    BE: '#f5f5dc',
    TRANG: '#ffffff',
    DEN: '#000000',
    XAM_NHAT: '#d3d3d3',
    XAM_DAM: '#808080',
    NAU_GO_NHAT: '#c8a165',
    NAU_GO_DAM: '#8b5a2b'
  };

  return colorMap[value] || '#ccc';
};

// Mock data
const mockProducts = [
  {
    id: 1,
    name: 'Sofa Mondrian 3 Chỗ',
    category: 'Phòng Khách',
    price: 28500000,
    originalPrice: 35000000,
    discount: 20,
    image: 'https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=800&h=600&fit=crop',
    rating: 4.8,
    reviews: 124,
    stock: 8,
    isNew: true,
    description: 'Sofa cao cấp với thiết kế hiện đại, chất liệu vải bố cao cấp'
  },
  {
    id: 2,
    name: 'Bàn Làm Việc Oak Premium',
    category: 'Phòng Làm Việc',
    price: 12800000,
    image: 'https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=800&h=600&fit=crop',
    rating: 4.9,
    reviews: 89,
    stock: 15,
    description: 'Bàn làm việc gỗ sồi tự nhiên, thiết kế tối giản'
  },
  {
    id: 3,
    name: 'Giường Ngủ Luxury King',
    category: 'Phòng Ngủ',
    price: 35900000,
    originalPrice: 42000000,
    discount: 15,
    image: 'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=800&h=600&fit=crop',
    rating: 5.0,
    reviews: 67,
    stock: 3,
    description: 'Giường ngủ cao cấp với đầu giường bọc da thật'
  },
  {
    id: 4,
    name: 'Bộ Bàn Ăn Marble 6 Ghế',
    category: 'Phòng Ăn',
    price: 45200000,
    image: 'https://images.unsplash.com/photo-1617806118233-18e1de247200?w=800&h=600&fit=crop',
    rating: 4.7,
    reviews: 92,
    stock: 0,
    description: 'Bộ bàn ăn mặt đá marble tự nhiên sang trọng'
  },
  {
    id: 5,
    name: 'Tủ Quần Áo 4 Cánh',
    category: 'Phòng Ngủ',
    price: 18900000,
    image: 'https://images.unsplash.com/photo-1595428774223-ef52624120d2?w=800&h=600&fit=crop',
    rating: 4.6,
    reviews: 56,
    stock: 12,
    isNew: true,
    description: 'Tủ quần áo gỗ công nghiệp cao cấp, thiết kế hiện đại'
  },
  {
    id: 6,
    name: 'Ghế Sofa Đơn Nordic',
    category: 'Phòng Khách',
    price: 8500000,
    image: 'https://images.unsplash.com/photo-1567538096630-e0c55bd6374c?w=800&h=600&fit=crop',
    rating: 4.8,
    reviews: 143,
    stock: 20,
    description: 'Ghế sofa đơn phong cách Bắc Âu tối giản'
  },
  {
    id: 7,
    name: 'Bàn Trà Gỗ Óc Chó',
    category: 'Phòng Khách',
    price: 15600000,
    originalPrice: 18000000,
    discount: 13,
    image: 'https://images.unsplash.com/photo-1540574163026-643ea20ade25?w=800&h=600&fit=crop',
    rating: 4.9,
    reviews: 78,
    stock: 6,
    description: 'Bàn trà gỗ óc chó tự nhiên, vân gỗ đẹp tự nhiên'
  },
  {
    id: 8,
    name: 'Kệ Sách Treo Tường',
    category: 'Phòng Làm Việc',
    price: 6200000,
    image: 'https://images.unsplash.com/photo-1594620302200-9a762244a156?w=800&h=600&fit=crop',
    rating: 4.5,
    reviews: 34,
    stock: 25,
    description: 'Kệ sách treo tường kim loại kết hợp gỗ'
  },
];

// export { ProductCard, getColorCode };