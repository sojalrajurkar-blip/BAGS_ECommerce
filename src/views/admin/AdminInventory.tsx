import React, { useState } from 'react';
import { AdminStatusBadge } from '../../components/admin/AdminStatusBadge';
import { AdminModal } from '../../components/admin/AdminModal';
import { Plus, Search } from 'lucide-react';
import { useStore } from '../../context/StoreContext';
import type { Product } from '../../types';

interface AdminInventoryProps {
  productsList: Product[];
  setProductsList: React.Dispatch<React.SetStateAction<Product[]>>;
}

export const AdminInventory: React.FC<AdminInventoryProps> = ({
  productsList,
  setProductsList,
}) => {
  const { addToast } = useStore();
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [stockFilter, setStockFilter] = useState<string>('all');

  // Restock modal state
  const [restockProduct, setRestockProduct] = useState<Product | null>(null);
  const [addUnits, setAddUnits] = useState<number | string>(25);

  const totalUnits = productsList.reduce((sum, p) => sum + (p.stock || 0), 0);
  const lowStockProducts = productsList.filter((p) => p.stock > 0 && p.stock <= 10);
  const outOfStockProducts = productsList.filter((p) => p.stock === 0);

  const handleRestock = (e: React.FormEvent) => {
    e.preventDefault();
    if (!restockProduct || Number(addUnits) <= 0) return;

    setProductsList((prev) =>
      prev.map((p) =>
        p.id === restockProduct.id
          ? { ...p, stock: (p.stock || 0) + Number(addUnits) }
          : p
      )
    );

    addToast(`Restocked +${addUnits} units for "${restockProduct.name}".`);
    setRestockProduct(null);
  };

  const filtered = productsList.filter((p) => {
    const matchesSearch =
      p.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      p.sku?.toLowerCase().includes(searchQuery.toLowerCase());
    if (stockFilter === 'low') return matchesSearch && p.stock > 0 && p.stock <= 10;
    if (stockFilter === 'out') return matchesSearch && p.stock === 0;
    return matchesSearch;
  });

  return (
    <div className="admin-page-content">
      {/* Inventory KPI Summary */}
      <div className="inventory-stats-row">
        <div className="inventory-stat-box">
          <span className="inv-stat-label">Total Warehouse Stock</span>
          <span className="inv-stat-value font-serif">{totalUnits.toLocaleString()}</span>
          <span className="inv-stat-sub">Units on hand across all SKUs</span>
        </div>

        <div className="inventory-stat-box">
          <span className="inv-stat-label">Low Stock Warnings</span>
          <span className="inv-stat-value font-serif text-warning">
            {lowStockProducts.length}
          </span>
          <span className="inv-stat-sub">SKUs below 10 unit threshold</span>
        </div>

        <div className="inventory-stat-box">
          <span className="inv-stat-label">Out of Stock Items</span>
          <span className="inv-stat-value font-serif text-error">
            {outOfStockProducts.length}
          </span>
          <span className="inv-stat-sub">Urgent studio production required</span>
        </div>
      </div>

      {/* Controls Bar */}
      <div className="admin-controls-bar">
        <div className="controls-left">
          <div className="admin-search-box">
            <Search size={16} className="search-icon" />
            <input
              type="text"
              placeholder="Search by product name or SKU..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="admin-input"
            />
          </div>

          <div className="stock-filter-buttons">
            <button
              className={`filter-tab ${stockFilter === 'all' ? 'filter-active' : ''}`}
              onClick={() => setStockFilter('all')}
            >
              All Items ({productsList.length})
            </button>
            <button
              className={`filter-tab ${stockFilter === 'low' ? 'filter-active' : ''}`}
              onClick={() => setStockFilter('low')}
            >
              Low Stock ({lowStockProducts.length})
            </button>
            <button
              className={`filter-tab ${stockFilter === 'out' ? 'filter-active' : ''}`}
              onClick={() => setStockFilter('out')}
            >
              Out of Stock ({outOfStockProducts.length})
            </button>
          </div>
        </div>
      </div>

      {/* Inventory Table */}
      <div className="admin-panel">
        <div className="admin-table-container">
          <table className="admin-table">
            <thead>
              <tr>
                <th>Product & SKU</th>
                <th>Category</th>
                <th>Current Stock</th>
                <th>Reorder Threshold</th>
                <th>Health Status</th>
                <th className="text-right">Restock</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map((p) => {
                const img = (p.colors && p.colors[0]?.image) || p.images?.[0];
                const isLow = p.stock > 0 && p.stock <= 10;
                const isOut = p.stock === 0;

                return (
                  <tr key={p.id}>
                    <td>
                      <div className="table-product-cell">
                        <img src={img} alt={p.name} className="table-thumb" />
                        <div>
                          <strong className="table-item-title">{p.name}</strong>
                          <span className="sku-code">{p.sku || 'RRA-STD-01'}</span>
                        </div>
                      </div>
                    </td>
                    <td>
                      <span className="category-pill">{p.category}</span>
                    </td>
                    <td>
                      <strong className={`text-base ${isOut ? 'text-error' : isLow ? 'text-warning' : 'text-primary'}`}>
                        {p.stock} units
                      </strong>
                    </td>
                    <td>10 units</td>
                    <td>
                      <AdminStatusBadge
                        status={isOut ? 'Out of Stock' : isLow ? 'Low Stock' : 'In Stock'}
                      />
                    </td>
                    <td className="text-right">
                      <button
                        className="btn btn-secondary btn-sm"
                        onClick={() => {
                          setRestockProduct(p);
                          setAddUnits(25);
                        }}
                      >
                        <Plus size={14} /> Restock
                      </button>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>

      {/* Restock Dialog Modal */}
      <AdminModal
        isOpen={Boolean(restockProduct)}
        onClose={() => setRestockProduct(null)}
        title={`Restock Inventory: ${restockProduct?.name}`}
      >
        <form onSubmit={handleRestock} className="admin-form">
          <p className="text-sm text-secondary mb-16">
            Current stock on hand: <strong>{restockProduct?.stock} units</strong>. Specify incoming batch size from the workshop:
          </p>

          <div className="form-group">
            <label className="form-label">Units to Add to Live Catalog</label>
            <input
              type="number"
              required
              min={1}
              className="form-input"
              value={addUnits}
              onChange={(e) => setAddUnits(Number(e.target.value))}
            />
          </div>

          <div className="quick-add-chips">
            <button type="button" className="chip-btn" onClick={() => setAddUnits(10)}>
              +10 Units
            </button>
            <button type="button" className="chip-btn" onClick={() => setAddUnits(25)}>
              +25 Units
            </button>
            <button type="button" className="chip-btn" onClick={() => setAddUnits(50)}>
              +50 Units
            </button>
            <button type="button" className="chip-btn" onClick={() => setAddUnits(100)}>
              +100 Units
            </button>
          </div>

          <div className="modal-actions-footer">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => setRestockProduct(null)}
            >
              Cancel
            </button>
            <button type="submit" className="btn btn-primary">
              Confirm Inward Batch (+{addUnits})
            </button>
          </div>
        </form>
      </AdminModal>
    </div>
  );
};
