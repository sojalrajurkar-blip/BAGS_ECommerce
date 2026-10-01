'use client';

import React from 'react';
import { useStore } from '../../context/StoreContext';
import { CheckCircle2, AlertCircle } from 'lucide-react';

export const ToastContainer: React.FC = () => {
  const { toasts } = useStore();

  if (toasts.length === 0) return null;

  return (
    <div className="toast-container" aria-live="polite">
      {toasts.map((toast) => (
        <div key={toast.id} className={`toast toast-${toast.type}`}>
          {toast.type === 'error' ? (
            <AlertCircle size={16} className="toast-icon-error" />
          ) : (
            <CheckCircle2 size={16} className="toast-icon-success" />
          )}
          <span>{toast.message}</span>
        </div>
      ))}
    </div>
  );
};
