import React, { useState } from 'react';
import { MOCK_CMS } from '../../data/mockData';
import { Save } from 'lucide-react';
import { useStore } from '../../context/StoreContext';
import type { CMSContent } from '../../types';

export const AdminCMS: React.FC = () => {
  const { addToast } = useStore();
  const [cmsData, setCmsData] = useState<CMSContent>(MOCK_CMS);

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    addToast('Store editorial content and banners updated successfully.');
  };

  return (
    <div className="admin-page-content">
      <div className="admin-controls-bar">
        <div>
          <h2 className="font-serif text-xl">Editorial Content & Storefront CMS</h2>
          <p className="text-sm text-muted">
            Configure announcement banners, brand storytelling paragraphs, and landing page highlights
          </p>
        </div>
      </div>

      <form onSubmit={handleSave} className="admin-cms-form">
        {/* Announcement Bar Section */}
        <div className="admin-panel p-6">
          <h3 className="panel-title font-serif mb-16">Top Announcement Bar</h3>
          <div className="form-group">
            <label className="form-label">Ticker Notification Copy</label>
            <input
              type="text"
              className="form-input"
              value={cmsData.announcementBar.text}
              onChange={(e) =>
                setCmsData({
                  ...cmsData,
                  announcementBar: { ...cmsData.announcementBar, text: e.target.value },
                })
              }
            />
          </div>
        </div>

        {/* Hero Banner Section */}
        <div className="admin-panel p-6">
          <h3 className="panel-title font-serif mb-16">Homepage Hero Section</h3>
          <div className="form-group">
            <label className="form-label">Hero Eyebrow</label>
            <input
              type="text"
              className="form-input"
              value={cmsData.heroBanner.eyebrow}
              onChange={(e) =>
                setCmsData({
                  ...cmsData,
                  heroBanner: { ...cmsData.heroBanner, eyebrow: e.target.value },
                })
              }
            />
          </div>

          <div className="form-group">
            <label className="form-label">Main Headline</label>
            <input
              type="text"
              className="form-input"
              value={cmsData.heroBanner.title}
              onChange={(e) =>
                setCmsData({
                  ...cmsData,
                  heroBanner: { ...cmsData.heroBanner, title: e.target.value },
                })
              }
            />
          </div>

          <div className="form-group">
            <label className="form-label">Supporting Editorial Paragraph</label>
            <textarea
              rows={3}
              className="form-textarea"
              value={cmsData.heroBanner.subtitle}
              onChange={(e) =>
                setCmsData({
                  ...cmsData,
                  heroBanner: { ...cmsData.heroBanner, subtitle: e.target.value },
                })
              }
            />
          </div>
        </div>

        {/* Craftsmanship Narrative */}
        <div className="admin-panel p-6">
          <h3 className="panel-title font-serif mb-16">Studio Craftsmanship Feature</h3>
          <div className="form-group">
            <label className="form-label">Feature Heading</label>
            <input
              type="text"
              className="form-input"
              value={cmsData.craftsmanshipFeature.heading}
              onChange={(e) =>
                setCmsData({
                  ...cmsData,
                  craftsmanshipFeature: {
                    ...cmsData.craftsmanshipFeature,
                    heading: e.target.value,
                  },
                })
              }
            />
          </div>

          <div className="form-group">
            <label className="form-label">Brand Philosophy Body</label>
            <textarea
              rows={4}
              className="form-textarea"
              value={cmsData.craftsmanshipFeature.paragraph1}
              onChange={(e) =>
                setCmsData({
                  ...cmsData,
                  craftsmanshipFeature: {
                    ...cmsData.craftsmanshipFeature,
                    paragraph1: e.target.value,
                  },
                })
              }
            />
          </div>
        </div>

        <div className="flex justify-end mt-16">
          <button type="submit" className="btn btn-primary">
            <Save size={16} /> Publish CMS Changes
          </button>
        </div>
      </form>
    </div>
  );
};
