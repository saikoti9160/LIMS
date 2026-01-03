

import React, { useState } from 'react';
import axios from '../../services/api';
import InputField from '../Homepage/InputField';
import './ForgotPassword.css';
import SignInPageImg from '../../assets/images/sign-in-page-img.svg';
import mailIcon from '../../assets/icons/mail-icon.svg';
import miniLogo from '../../assets/images/mini-logo.svg';
import lockIcon from '../../assets/icons/lock-icon.svg';
import Header from '../Homepage/Header';
import { useNavigate } from 'react-router-dom';
import { authenticationService } from '../../services/AuthenticationService';


const ForgotPassword = () => {
  const [email, setEmail] = useState('');
  const [message, setMessage] = useState('');
  const [countdown, setCountdown] = useState(0);
  const [isCounting, setIsCounting] = useState(false);
  const [showResend, setShowResend] = useState(false);
   const navigate = useNavigate();
  // const navigate = useNavigate();

  const startCountdown = () => {
    let timeLeft = 49;
    setCountdown(timeLeft);
    setIsCounting(true);
    setShowResend(false);

    const interval = setInterval(() => {
      timeLeft -= 1;
      setCountdown(timeLeft);

      if (timeLeft <= 0) {
        clearInterval(interval);
        setIsCounting(false);
        setShowResend(true);
      }
    }, 1000);
  };

  const handleForgotPassword = async (e) => {
    e.preventDefault();
    if (!email) {
      setMessage("Please enter your email.");
      return;
    }

    try {
      await authenticationService.forgotPassword(email);
      startCountdown();


    } catch (error) {
      setMessage('Error occurred while resetting the password.');
    }
  };

  return (
    <div>

      <div className="forgot-container">
        <div className="forgot-inner-container">
          <div className="signin-image2">
            <img src={SignInPageImg} alt="Sign In" />
          </div>
          <div className="forget-content">
            <div className="forgot-card">
              <img src={miniLogo} alt="Logo" className='mini-logo' />
              <h2>Forgot Password</h2>
              <form onSubmit={handleForgotPassword} className='form1'>
                <InputField

                  label="Email Address"
                  type="email"
                  placeholder="labbvion@gmail.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  img={mailIcon}
                  autoComplete={true}
                />
                {message && <p className="info-message">{message}</p>}

                <div className="button-containers">
                  {!isCounting && !showResend ? (
                    <button type="submit" className="send-button">Send</button>
                  ) : isCounting ? (
                    <p className="countdown-text">
                      Resend in <span>0:{countdown < 10 ? `0${countdown}` : countdown}</span>
                    </p>
                  ) : (
                    <button type="button" className="resend-button" onClick={handleForgotPassword}>Resend</button>
                  )}
                  <button type="button" className="go-to-home"onClick={()=>navigate('/')} >Go to Home</button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ForgotPassword;
