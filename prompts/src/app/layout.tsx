import type { Metadata, Viewport } from 'next';
import { Inter, Cormorant_Garamond } from 'next/font/google';
import '../index.css';
import '../styles/components.css';
import '../styles/pages.css';
import '../styles/admin.css';

import { StoreProvider } from '../context/StoreContext';
import { LenisProvider } from '../animations/LenisProvider';
import { Header } from '../components/common/Header';
import { Footer } from '../components/common/Footer';
import { CartDrawer } from '../components/common/CartDrawer';
import { SearchModal } from '../components/common/SearchModal';
import { ToastContainer } from '../components/common/ToastContainer';

const inter = Inter({
  subsets: ['latin'],
  variable: '--font-inter',
  display: 'swap',
});

const cormorantGaramond = Cormorant_Garamond({
  subsets: ['latin'],
  weight: ['400', '500', '600', '700'],
  style: ['normal', 'italic'],
  variable: '--font-cormorant',
  display: 'swap',
});

export const metadata: Metadata = {
  metadataBase: new URL('https://rorastudios.com'),
  title: {
    default: 'RÓRA — Handcrafted Luxury Bags & Architectural Carry Essentials',
    template: '%s | RÓRA',
  },
  description:
    'Explore RÓRA’s collection of handcrafted luxury backpacks, weekenders, totes, and daily briefcases. Built with sustainable ocean-bound textiles and vegetable-tanned Tuscan leathers.',
  keywords: [
    'luxury bags',
    'luxury backpacks',
    'leather carry essentials',
    'minimalist totes',
    'weekenders',
    'travel bags',
    'Tuscan leather',
    'architectural carry',
    'sustainable luxury',
  ],
  authors: [{ name: 'RÓRA Studio A/S' }],
  creator: 'RÓRA Studio A/S',
  publisher: 'RÓRA Studio A/S',
  openGraph: {
    type: 'website',
    locale: 'en_US',
    url: 'https://rorastudios.com/',
    siteName: 'RÓRA',
    title: 'RÓRA — Handcrafted Luxury Bags & Carry Essentials',
    description: 'Thoughtfully designed carry essentials built with sustainable textiles and vegetable-tanned leathers.',
    images: [
      {
        url: 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=1200&q=80',
        width: 1200,
        height: 630,
        alt: 'RÓRA Handcrafted Bags',
      },
    ],
  },
  twitter: {
    card: 'summary_large_image',
    title: 'RÓRA — Handcrafted Luxury Bags & Carry Essentials',
    description: 'Thoughtfully designed carry essentials built with sustainable textiles and vegetable-tanned leathers.',
    images: ['https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=1200&q=80'],
  },
  icons: {
    icon: "data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%231E1D1A' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><path d='M6 2 3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4Z'/><path d='M3 6h18'/><path d='M16 10a4 4 0 0 1-8 0'/></svg>",
  },
};

export const viewport: Viewport = {
  themeColor: '#F7F4EE',
  width: 'device-width',
  initialScale: 1,
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" className={`${inter.variable} ${cormorantGaramond.variable}`}>
      <body className="antialiased">
        <StoreProvider>
          <LenisProvider>
            <div className="site-wrapper">
              <Header />
              <main id="main-content">{children}</main>
              <Footer />
              <CartDrawer />
              <SearchModal />
              <ToastContainer />
            </div>
          </LenisProvider>
        </StoreProvider>
      </body>
    </html>
  );
}
