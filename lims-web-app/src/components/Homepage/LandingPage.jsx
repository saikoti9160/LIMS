import { Link, Route, Routes } from "react-router-dom";
import Header from "./Header";
import React from "react";
import { useSelector } from "react-redux";
import Sidebar from "../Sidebar/Sidebar";
import MyProfile from "./MyProfile";
// import UserMaster from "../UserMaster/UserMaster";
import ForgotPassword from "../Authentication/ForgotPassword";
import SignInPage from "../Authentication/SignInPage";
import LabManagement from "../pages/platform-owners/LabManagement/LabManagement";
import PremiumPage from "./PremiumPage";
import FreeSamples from "./HomePage";


const LandingPage = () => {
    const { isPremium } = useSelector((state) => state.subscription);
  
    return (
      <div>
        {/* <Header/> */}
          
        {/* <Sidebar /> */}

        {/* <header style={{  padding: '20px', textAlign: 'center' }}>
          <h1>Laboratory Information Management System (LIMS)</h1>
          <nav>
            <Link to="/" style={{ margin: '0 10px', color: 'white' }}>Dashboard</Link>
            <Link to="/samples" style={{ margin: '0 10px', color: 'white' }}>Samples</Link>
            <Link to="/reports" style={{ margin: '0 10px', color: 'white' }}>Reports</Link>
            <Link to="/inventory" style={{ margin: '0 10px', color: 'white' }}>Inventory</Link>
            <Link to="/contact" style={{ margin: '0 10px', color: 'white' }}>Contact Support</Link>
          </nav>
        </header>
  
        <main style={{ flex: 1, padding: '20px' }}>
          <h2>Welcome to the LIMS Dashboard!</h2>
          <p>Manage and track laboratory samples, test results, and reports efficiently.</p>
  
          <section style={{ marginTop: '20px' }}>
            <h3>Sample Tracking</h3>
            <p>Monitor the status of samples in the lab, track analysis progress, and view results.</p>
            <Link to="/samples" style={{ color: '#007bff' }}>View Sample List</Link>
          </section>
  
          <section style={{ marginTop: '20px' }}>
            <h3>Test Results</h3>
            {!isPremium ? (
              <div>
                <p>You need a premium subscription to access test results analysis.</p>
                <Link to="/subscribe" style={{ color: '#007bff' }}>Subscribe Now</Link>
              </div>
            ) : (
              <p>Access detailed test results and analytics for your samples.</p>
            )}
          </section>
  
          <section style={{ marginTop: '20px' }}>
            <h3>Inventory Management</h3>
            <p>Track lab equipment, chemicals, and supplies, ensuring you have what you need for tests.</p>
            <Link to="/inventory" style={{ color: '#007bff' }}>View Inventory</Link>
          </section>
  
          <section style={{ marginTop: '20px' }}>
            <h3>Lab Reports</h3>
            <p>Generate and manage lab reports based on the analysis of your samples.</p>
            <Link to="/reports" style={{ color: '#007bff' }}>View Reports</Link>
          </section>
        </main> */}
  
        {/* <footer style={{ background: '#282c34', color: 'white', padding: '20px', textAlign: 'center' }}>
          <p>&copy; 2025 LIMS System. All Rights Reserved.</p>
        </footer> */}
      </div>
    );
  };
  
  export default LandingPage;
  