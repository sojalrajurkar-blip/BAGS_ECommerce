'use client';

import React from 'react';
import { useStore } from '../../context/StoreContext';
import { ChevronRight } from 'lucide-react';

export interface BreadcrumbItem {
  label: string;
  page?: string;
  params?: Record<string, unknown>;
}

export interface BreadcrumbsProps {
  items?: BreadcrumbItem[];
}

export const Breadcrumbs: React.FC<BreadcrumbsProps> = ({ items = [] }) => {
  const { navigate } = useStore();

  return (
    <nav className="breadcrumbs-nav" aria-label="Breadcrumb">
      <button className="breadcrumb-item" onClick={() => navigate('home')}>
        Home
      </button>
      {items.map((item, index) => {
        const isLast = index === items.length - 1;
        return (
          <React.Fragment key={index}>
            <ChevronRight size={13} className="breadcrumb-separator" />
            {isLast || !item.page ? (
              <span className="breadcrumb-item breadcrumb-current">{item.label}</span>
            ) : (
              <button
                className="breadcrumb-item"
                onClick={() => navigate(item.page!, item.params || {})}
              >
                {item.label}
              </button>
            )}
          </React.Fragment>
        );
      })}
    </nav>
  );
};
