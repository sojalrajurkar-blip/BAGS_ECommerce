import React, { useState } from 'react';
import { CATEGORIES } from '../../data/mockData';
import { AdminModal } from '../../components/admin/AdminModal';
import { AdminStatusBadge } from '../../components/admin/AdminStatusBadge';
import { Plus, Edit2, Trash2, Search, Filter } from 'lucide-react';
import { useStore } from '../../context/StoreContext';
import type { Product } from '../../types';

interface AdminProductsProps {
  productsList: Product[];
  setProductsList: React.Dispatch<React.SetStateAction<Product[]>>;
}

interface ProductFormData {
  name: string;
  category: string;
  price: string | number;
  originalPrice: string | number;
  stock: number;
  material: string;
  capacity: string;
  tagline: string;
  description: string;
  imageUrl: string;
}

export const AdminProducts: React.FC<AdminProductsProps> = ({
  productsList,
  setProductsList,
}) => {
  const { addToast } = useStore();
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [selectedCategory, setSelectedCategory] = useState<string>('all');

  // Modal State
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [editingProduct, setEditingProduct] = useState<Product | null>(null);

  // Form State
  const [formData, setFormData] = useState<ProductFormData>({
    name: '',
    category: 'backpacks',
    price: '',
    originalPrice: '',
    stock: 20,
    material: 'Weather-resistant 900D Recycled Nylon',
    capacity: '20L',
    tagline: 'Adventure-ready. Everyday style.',
    description: 'Precision-crafted luxury bag engineered for modern journeys.',
    imageUrl: 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1000&q=80',
  });

  const handleOpenAdd = () => {
    setEditingProduct(null);
    setFormData({
      name: '',
      category: 'backpacks',
      price: '',
      originalPrice: '',
      stock: 20,
      material: 'Weather-resistant 900D Recycled Nylon',
      capacity: '20L',
      tagline: 'Adventure-ready. Everyday style.',
      description: 'Precision-crafted luxury bag engineered for modern journeys.',
      imageUrl: 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1000&q=80',
    });
    setIsModalOpen(true);
  };

  const handleOpenEdit = (product: Product) => {
    setEditingProduct(product);
    setFormData({
      name: product.name,
      category: product.category,
      price: product.price,
      originalPrice: product.originalPrice || product.price,
      stock: product.stock,
      material: product.material,
      capacity: product.capacity || '20L',
      tagline: product.tagline || '',
      description: product.description || '',
      imageUrl: (product.colors && product.colors[0]?.image) || product.images?.[0] || '',
    });
    setIsModalOpen(true);
  };

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.name || !formData.price) {
      addToast('Please provide a product title and price.', 'error');
      return;
    }

    if (editingProduct) {
      // Update existing
      setProductsList((prev) =>
        prev.map((p) =>
          p.id === editingProduct.id
            ? {
                ...p,
                name: formData.name,
                category: formData.category,
                price: Number(formData.price),
                originalPrice: Number(formData.originalPrice || formData.price),
                stock: Number(formData.stock),
                material: formData.material,
                capacity: formData.capacity,
                tagline: formData.tagline,
                description: formData.description,
              }
            : p
        )
      );
      addToast(`Updated product "${formData.name}".`);
    } else {
      // Create new
      const newId = `prod-${Date.now()}`;
      const newProduct: Product = {
        id: newId,
        name: formData.name,
        slug: formData.name.toLowerCase().replace(/\s+/g, '-'),
        tagline: formData.tagline,
        category: formData.category,
        price: Number(formData.price),
        originalPrice: Number(formData.originalPrice || formData.price),
        discount: 0,
        rating: 5.0,
        reviewCount: 0,
        badge: 'New Release',
        stock: Number(formData.stock),
        sku: `RRA-${formData.category.substring(0, 3).toUpperCase()}-${Math.floor(10 + Math.random() * 90)}`,
        material: formData.material,
        capacity: formData.capacity,
        dimensions: '46 × 30 × 16 cm',
        colors: [
          { name: 'Standard Edition', hex: '#1E1D1A', image: formData.imageUrl },
        ],
        images: [formData.imageUrl],
        description: formData.description,
        features: ['Architectural silhouette', 'Water-repellent finish', 'Artisanal Italian hardware'],
        specifications: { Origin: 'Handcrafted in Studio', Warranty: 'Lifetime Repair Guarantee' },
      };
      setProductsList((prev) => [newProduct, ...prev]);
      addToast(`Created product "${newProduct.name}".`);
    }
    setIsModalOpen(false);
  };

  const handleDelete = (id: string, name: string) => {
    if (window.confirm(`Are you sure you want to remove "${name}" from the catalog?`)) {
      setProductsList((prev) => prev.filter((p) => p.id !== id));
      addToast(`Removed "${name}" from catalog.`);
    }
  };

  const filteredProducts = productsList.filter((p) => {
    const matchesSearch =
      p.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      p.sku?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      p.category.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesCat = selectedCategory === 'all' || p.category === selectedCategory;
    return matchesSearch && matchesCat;
  });

  return (
    <div className="admin-page-content">
      {/* Table Controls Topbar */}
      <div className="admin-controls-bar">
        <div className="controls-left">
          <div className="admin-search-box">
            <Search size={16} className="search-icon" />
            <input
              type="text"
              placeholder="Search by name, SKU or material..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="admin-input"
            />
          </div>

          <div className="admin-filter-box">
            <Filter size={15} />
            <select
              value={selectedCategory}
              onChange={(e) => setSelectedCategory(e.target.value)}
              className="admin-select"
            >
              <option value="all">All Categories ({productsList.length})</option>
              {CATEGORIES.map((cat) => (
                <option key={cat.id} value={cat.id}>
                  {cat.name}
                </option>
              ))}
            </select>
          </div>
        </div>

        <button className="btn btn-primary btn-sm" onClick={handleOpenAdd}>
          <Plus size={16} /> Add Product
        </button>
      </div>

      {/* Products Table */}
      <div className="admin-panel">
        <div className="admin-table-container">
          <table className="admin-table">
            <thead>
              <tr>
                <th>Product Details</th>
                <th>Category</th>
                <th>SKU</th>
                <th>Price (INR)</th>
                <th>Stock</th>
                <th>Status</th>
                <th className="text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredProducts.length === 0 ? (
                <tr>
                  <td colSpan={7} className="text-center p-40">
                    <p className="text-muted">No products match your search filter.</p>
                  </td>
                </tr>
              ) : (
                filteredProducts.map((p) => {
                  const img = (p.colors && p.colors[0]?.image) || p.images?.[0];
                  return (
                    <tr key={p.id}>
                      <td>
                        <div className="table-product-cell">
                          <img src={img} alt={p.name} className="table-thumb" />
                          <div>
                            <strong className="table-item-title">{p.name}</strong>
                            <span className="table-sub-text">{p.tagline}</span>
                          </div>
                        </div>
                      </td>
                      <td>
                        <span className="category-pill">{p.category}</span>
                      </td>
                      <td>
                        <span className="sku-code">{p.sku || 'RRA-STD-01'}</span>
                      </td>
                      <td>
                        <strong>₹{p.price?.toLocaleString('en-IN')}</strong>
                        {p.originalPrice > p.price && (
                          <span className="table-original-price">
                            ₹{p.originalPrice?.toLocaleString('en-IN')}
                          </span>
                        )}
                      </td>
                      <td>
                        <span>{p.stock} units</span>
                      </td>
                      <td>
                        <AdminStatusBadge
                          status={p.stock === 0 ? 'Out of Stock' : p.stock < 10 ? 'Low Stock' : 'In Stock'}
                        />
                      </td>
                      <td className="text-right">
                        <div className="table-actions-row">
                          <button
                            className="table-icon-btn"
                            onClick={() => handleOpenEdit(p)}
                            title="Edit Product"
                            aria-label="Edit Product"
                          >
                            <Edit2 size={15} />
                          </button>
                          <button
                            className="table-icon-btn delete-btn"
                            onClick={() => handleDelete(p.id, p.name)}
                            title="Delete Product"
                            aria-label="Delete Product"
                          >
                            <Trash2 size={15} />
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Add / Edit Product Modal */}
      <AdminModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={editingProduct ? `Edit ${editingProduct.name}` : 'Add New Handcrafted Bag'}
      >
        <form onSubmit={handleSave} className="admin-form">
          <div className="form-group">
            <label className="form-label">Product Name</label>
            <input
              type="text"
              required
              className="form-input"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              placeholder="e.g. The Atelier Tote"
            />
          </div>

          <div className="form-row-2">
            <div className="form-group">
              <label className="form-label">Category</label>
              <select
                className="form-select"
                value={formData.category}
                onChange={(e) => setFormData({ ...formData, category: e.target.value })}
              >
                {CATEGORIES.map((cat) => (
                  <option key={cat.id} value={cat.id}>
                    {cat.name}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label className="form-label">Price (INR ₹)</label>
              <input
                type="number"
                required
                className="form-input"
                value={formData.price}
                onChange={(e) => setFormData({ ...formData, price: e.target.value })}
                placeholder="4899"
              />
            </div>
          </div>

          <div className="form-row-2">
            <div className="form-group">
              <label className="form-label">Stock Quantity</label>
              <input
                type="number"
                required
                className="form-input"
                value={formData.stock}
                onChange={(e) => setFormData({ ...formData, stock: Number(e.target.value) })}
              />
            </div>

            <div className="form-group">
              <label className="form-label">Capacity / Size</label>
              <input
                type="text"
                className="form-input"
                value={formData.capacity}
                onChange={(e) => setFormData({ ...formData, capacity: e.target.value })}
                placeholder="20L or One Size"
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Material & Construction</label>
            <input
              type="text"
              className="form-input"
              value={formData.material}
              onChange={(e) => setFormData({ ...formData, material: e.target.value })}
              placeholder="e.g. Weatherproof Recycled Japanese Nylon"
            />
          </div>

          <div className="form-group">
            <label className="form-label">Image URL</label>
            <input
              type="url"
              className="form-input"
              value={formData.imageUrl}
              onChange={(e) => setFormData({ ...formData, imageUrl: e.target.value })}
            />
          </div>

          <div className="modal-actions-footer">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => setIsModalOpen(false)}
            >
              Cancel
            </button>
            <button type="submit" className="btn btn-primary">
              {editingProduct ? 'Save Changes' : 'Create Product'}
            </button>
          </div>
        </form>
      </AdminModal>
    </div>
  );
};
