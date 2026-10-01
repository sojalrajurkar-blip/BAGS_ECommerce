import React, { useState } from 'react';
import { CATEGORIES } from '../../data/mockData';
import { AdminModal } from '../../components/admin/AdminModal';
import { Plus, Edit2 } from 'lucide-react';
import { useStore } from '../../context/StoreContext';
import type { Category } from '../../types';

interface AdminCategoriesProps {
  categoriesList?: Category[];
  setCategoriesList?: React.Dispatch<React.SetStateAction<Category[]>>;
}

export const AdminCategories: React.FC<AdminCategoriesProps> = ({
  categoriesList = CATEGORIES,
  setCategoriesList,
}) => {
  const { addToast } = useStore();
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [editingCategory, setEditingCategory] = useState<Category | null>(null);

  const [formData, setFormData] = useState({
    name: '',
    slug: '',
    headline: '',
    description: '',
    heroImage: 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1600&q=80',
    count: 10,
  });

  const handleOpenAdd = () => {
    setEditingCategory(null);
    setFormData({
      name: '',
      slug: '',
      headline: '',
      description: '',
      heroImage: 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1600&q=80',
      count: 10,
    });
    setIsModalOpen(true);
  };

  const handleOpenEdit = (cat: Category) => {
    setEditingCategory(cat);
    setFormData({
      name: cat.name,
      slug: cat.slug,
      headline: cat.headline || '',
      description: cat.description,
      heroImage: cat.heroImage,
      count: cat.count,
    });
    setIsModalOpen(true);
  };

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.name) return;

    if (editingCategory) {
      if (setCategoriesList) {
        setCategoriesList((prev) =>
          prev.map((c) => (c.id === editingCategory.id ? { ...c, ...formData } : c))
        );
      }
      addToast(`Updated category "${formData.name}".`);
    } else {
      const newCat: Category = {
        id: formData.name.toLowerCase().replace(/\s+/g, '-'),
        ...formData,
      };
      if (setCategoriesList) {
        setCategoriesList((prev) => [...prev, newCat]);
      }
      addToast(`Created category "${newCat.name}".`);
    }
    setIsModalOpen(false);
  };

  return (
    <div className="admin-page-content">
      <div className="admin-controls-bar">
        <div>
          <h2 className="font-serif text-xl">Collection Categories ({categoriesList.length})</h2>
          <p className="text-sm text-muted">
            Curated bag silhouettes and taxonomy displayed across the customer navigation
          </p>
        </div>
        <button className="btn btn-primary btn-sm" onClick={handleOpenAdd}>
          <Plus size={16} /> Add Category
        </button>
      </div>

      <div className="admin-categories-grid">
        {categoriesList.map((cat) => (
          <div key={cat.id} className="category-admin-card">
            <div className="category-card-img-wrap">
              <img src={cat.heroImage} alt={cat.name} className="category-card-img" />
              <span className="category-card-badge">{cat.count} Products</span>
            </div>
            <div className="category-card-body">
              <div className="category-card-header">
                <div>
                  <h3 className="category-card-title font-serif">{cat.name}</h3>
                  <span className="category-slug-text">/{cat.slug}</span>
                </div>
                <div className="category-actions">
                  <button
                    className="table-icon-btn"
                    onClick={() => handleOpenEdit(cat)}
                    title="Edit Category"
                    aria-label="Edit Category"
                  >
                    <Edit2 size={14} />
                  </button>
                </div>
              </div>
              <p className="category-card-headline">{cat.headline}</p>
              <p className="category-card-desc">{cat.description}</p>
            </div>
          </div>
        ))}
      </div>

      {/* Add / Edit Category Modal */}
      <AdminModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={editingCategory ? `Edit Category: ${editingCategory.name}` : 'Add New Category'}
      >
        <form onSubmit={handleSave} className="admin-form">
          <div className="form-group">
            <label className="form-label">Category Name</label>
            <input
              type="text"
              required
              className="form-input"
              value={formData.name}
              onChange={(e) => {
                const name = e.target.value;
                setFormData({
                  ...formData,
                  name,
                  slug: name.toLowerCase().replace(/\s+/g, '-'),
                });
              }}
              placeholder="e.g. Travel Duffels"
            />
          </div>

          <div className="form-group">
            <label className="form-label">URL Slug</label>
            <input
              type="text"
              required
              className="form-input"
              value={formData.slug}
              onChange={(e) => setFormData({ ...formData, slug: e.target.value })}
              placeholder="travel-duffels"
            />
          </div>

          <div className="form-group">
            <label className="form-label">Editorial Headline</label>
            <input
              type="text"
              className="form-input"
              value={formData.headline}
              onChange={(e) => setFormData({ ...formData, headline: e.target.value })}
              placeholder="e.g. Weekender & Expedition Duffels"
            />
          </div>

          <div className="form-group">
            <label className="form-label">Category Description</label>
            <textarea
              rows={3}
              className="form-textarea"
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
            />
          </div>

          <div className="form-group">
            <label className="form-label">Banner Image URL</label>
            <input
              type="url"
              className="form-input"
              value={formData.heroImage}
              onChange={(e) => setFormData({ ...formData, heroImage: e.target.value })}
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
              {editingCategory ? 'Save Changes' : 'Create Category'}
            </button>
          </div>
        </form>
      </AdminModal>
    </div>
  );
};
