import React from 'react';
import { useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import Header from './Header';

const PremiumPage = () => {
  const { isPremium } = useSelector((state) => state.subscription);

  return (
    <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      <Header/>
      <header style={{ background: ' #fed9b0', padding: '20px', textAlign: 'center' }}>
        <h1>Sample Full Content Page</h1>
        <nav>
          <Link to="/" style={{ margin: '0 10px', color: 'white' }}>Home</Link>
          <Link to="/about" style={{ margin: '0 10px', color: 'white' }}>About</Link>
          <Link to="/contact" style={{ margin: '0 10px', color: 'white' }}>Contact</Link>
        </nav>
      </header>

      <main style={{ flex: 1, padding: '20px' }}>
        <h2>Welcome to our sample page!</h2>
        <p>This page is designed to show how you can fill an entire screen with content.</p>

        <section style={{ marginTop: '20px' }}>
          <h3>Premium Content</h3>
          {!isPremium ? (
            <div>
              <p>You need a premium subscription to access this section.</p>
              <Link to="/subscribe" style={{ color: '#007bff' }}>Subscribe Now</Link>
            </div>
          ) : (
            <p>This is exclusive premium content just for you!</p>
          )}
        </section>

        <section style={{ marginTop: '20px' }}>
          <h3>Additional Information</h3>
          <p>Here you can add more sections, such as blog posts, news updates, or other content that fits your needs.</p>
        </section>
      </main>

      <footer style={{ background: '#282c34', color: 'white', padding: '20px', textAlign: 'center' }}>
        <p>&copy; 2025 My Sample Site. All Rights Reserved.</p>
      </footer>
    </div>
  );
};

export default PremiumPage;
