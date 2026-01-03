import React, { useState } from 'react';
import InputField from '../Homepage/InputField';
import { useDispatch } from 'react-redux';
import { setUser } from '../../store/slices/userSlice.js';
import SignInPageImg from '../../assets/images/sign-in-page-img.svg';
import './SignInPage.css';
import miniLogo from '../../assets/images/mini-logo.svg';
import mailIcon from '../../assets/icons/mail-icon.svg';
import lockIcon from '../../assets/icons/lock-icon.svg';
import { authenticationService } from '../../services/AuthenticationService.js';
import Checkbox from '../Re-usable-components/Checkbox.jsx';
import Error from '../Re-usable-components/Error.jsx';
import { useNavigate } from 'react-router-dom';

const SignInPage = ({ onChange }) => {
 
  const [isChecked, setIsChecked] = useState(false);
    const [formData, setFormData] = useState({
    email: "",
    password: "",
    rememberMe: false,
    errors: {},
  });
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const validateForm = () => {
    let errors = {};
  
    if (!formData.email.trim()) {
      errors.email = "Email is required";
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      errors.email = "Enter a valid email address";
    }
  
    if (!formData.password.trim()) {
      errors.password = "Password is required";
    }
  
    setFormData((prev) => ({ ...prev, errors }));
    return Object.keys(errors).length === 0;
  };
  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData({
      ...formData,
      [name]: type === "checkbox" ? checked : value,
      errors: { ...formData.errors, [name]: "" },
    });
  };

  const handleSignIn = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;
  
    try {
      const response = await authenticationService.login({
        email: formData.email,
        password: formData.password,
      });
  
      const { token } = response.data;
      
      dispatch(setUser(response.data));
      localStorage.setItem('userDetails', JSON.stringify(response.data));
      localStorage.setItem("authToken", token);
      onChange("signin");
    } catch (err) {
      let errorMessage = "Invalid email or password.";
  
      if (err.response?.data?.message) {
        errorMessage = err.response.data.message;
      }
  
      setFormData((prev) => ({
        ...prev,
        errors: { apiError: errorMessage },
      }));
    }
  };

  return (
    <div>
      <div className='signin-container'>
        <div className="signin-inner-container">
          <div className="signin-image">
            <img src={SignInPageImg} alt="Sign In" />
          </div>
          <div className='signin-form-container'>
            <div className="signin-form">
              <img src={miniLogo} alt="Logo" className='mini-logo' />
              <h2>Sign In</h2>
              <form onSubmit={handleSignIn}>
                <div className="signin-form-group ">
                  <InputField
                    label="Email Address"
                    type="email"
                    name="email"
                    placeholder="Email Address"
                    value={formData.email}
                    onChange={handleChange}
                    className={formData.errors.email ? "form-input error" : "form-input"}
                    img={mailIcon}
                    autoComplete={true}
                    width={'93%'}
                    margin-top={'10px'}
                  />
                 {formData.errors.email && <Error message={formData.errors.email} type="error" />}
                  <InputField
                    label="Password"
                    type="password"
                    name="password"
                    placeholder="Enter your password"
                    value={formData.password}
                    onChange={handleChange}
                    className={formData.errors.password ? "form-input error" : "form-input"}
                    img={lockIcon}
                    width={'93%'}
                    margin-top={'10px'}
                  />
                   {formData.errors.password && <Error message={formData.errors.password} type="error" />}
                   {formData.errors.apiError && <Error message={formData.errors.apiError} type="error" />}
                </div>
             
                <div className="form-options">
                  <div className="remember-me"> 
                    <Checkbox
                  checked={isChecked}        
                  onChange={setIsChecked}     
                  />
               <span>Remember me</span>
                </div>
                  <a href="/forgot-password" className="forgot-link">
                    Forgot Password?
                  </a>
                </div>
                <button type="submit" className="signin-form-btn">
                  Sign In
                </button>
                <button className="home-button" onClick={()=>navigate('/')}>
                Go to Home
              </button>
              </form>
             
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SignInPage;
