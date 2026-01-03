import React, { useState, useEffect, useRef } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { logout } from '../../store/slices/userSlice.js'; // Update the path as needed
import logo from '../../assets/images/logo.svg';
import downArrow from '../../assets/icons/down-arrow.svg';
import notificationsBell from '../../assets/icons/notifications-bell.svg';
import globe from '../../assets/icons/globe-black.svg';
import languageArrow from '../../assets/icons/language-arrow.svg';
import Search from '../Search/Search';

const Header = ({ onChange, isLogin }) => {
  
  const { user } = useSelector((state) => state.user);
  const isAuthenticated = isLogin;
  const dispatch = useDispatch();
  const navigate = useNavigate();
  
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const dropdownRef = useRef(null);

  const handleLogout = () => {
    dispatch(logout());
    localStorage.removeItem('authToken');
    onChange('header');
  };

  const handleNavigations = (name) => {
    switch (name) {
      case 'logout':
        handleLogout();
        break;
      case 'profile':
        navigate('/my-profile');
        break;
      case 'home':
        if(!isLogin) {
          navigate('/login');
          break;
        }
        navigate('/dashboard');
        break;
      case 'packages':
        navigate('/packages');
        break;

      default:
        break;
    }
    toggleDropdown();
  };

  const toggleDropdown = () => {
    setIsDropdownOpen((prev) => !prev);
  };

  const closeDropdown = (e) => {
    if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
      setIsDropdownOpen(false);
    }
  };

  useEffect(() => {
    document.addEventListener('mousedown', closeDropdown);
    return () => {
      document.removeEventListener('mousedown', closeDropdown);
    };
  }, []);

  return (
    <div>
      <header className="header-container">
        <div className="inner-header-container">
          <img onClick={() => handleNavigations('home')} src={logo} alt="logo" className="logo" />

          {!isAuthenticated ? (
            <div className="header-middle-section">
              <span onClick={() => handleNavigations('home')}>Home</span>
              <span>Features</span>
              <span>Contact Us</span>
              <span onClick={() => handleNavigations('packages')} >Packages</span>
            </div>
          ) : null}

          {isAuthenticated ? (
            <div className="header-right">
              <div className="header-search">
                <Search />
              </div>

              <div className="header-language">
                <img src={globe} alt="globe" />
                <span style={{ paddingLeft: '10px', paddingRight: '30px' }}>English</span>
                <img src={languageArrow} alt="arrow" />
              </div>

              <div className="notification-bell">
                <img src={notificationsBell} alt="notifications" width="18px" height="auto" />
              </div>

              <div className="profile-logo">
                <img src={user?.profilePic || logo} alt="profile logo" width="20px" height="50px" />
              </div>

              <div className="profile-info" ref={dropdownRef}>
                <span className="profile-name" onClick={toggleDropdown}>
                  {user?.userName} <img src={downArrow} alt="down arrow" />
                </span>
                <span className="profile-login-type" style={{ marginRight: '25px' }}>
                  {user?.role}
                </span>
                {isDropdownOpen && (
                  <div className="profile-login-type-options">
                    <span className="my-profile" onClick={() => handleNavigations('profile')}>
                      My Profile
                    </span>
                    <span className="logout" onClick={() => handleNavigations('logout')}>
                      Logout
                    </span>
                  </div>
                )}
              </div>
            </div>
          ) : (
            <div className="header-right">
              <button className="btn-signIn " onClick={() => navigate('/login')}> Sign in</button>
              <button  className='btn-primary' onClick={() => navigate('/sign-up')}>Sign up</button>
              <span className="book-demo-btn header-btn">Book a Demo</span>
            </div>
          )}
        </div>
      </header>
    </div>
  );
};

export default Header;
