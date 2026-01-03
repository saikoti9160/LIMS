
import React, { useState, useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import InputField from "../Homepage/InputField";
import miniLogo from "../../assets/images/mini-logo.svg";
import lockIcon from "../../assets/icons/lock-icon.svg";
import SignInPageImg from "../../assets/images/sign-in-page-img.svg";
import "./ResetPassword.css";
import { authenticationService } from "../../services/AuthenticationService";
import Error from "../Re-usable-components/Error";

const ResetPassword = () => {
  const [resetForm, setResetForm] = useState({
    password: "",
    confirmPassword: "",
    errors: {},
  });
  const [passwordStrength, setPasswordStrength] = useState("");
  const [loading, setLoading] = useState(false);
  const [showResetForm, setShowResetForm] = useState(false);
  const [isResetSuccess, setIsResetSuccess] = useState(false);
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const verificationCode = searchParams.get("confirmation_code");
  const email = searchParams.get("user_name");
  const expiryTime = searchParams.get("expiry_time");

  useEffect(() => {
    if (!verificationCode || !email || !expiryTime) {
      setResetForm((prev) => ({
        ...prev,
        errors: { apiError: "Invalid or expired reset link." },
      }));
    }
  }, [verificationCode, email, expiryTime]);

  // Validate form
  const validateForm = () => {
    let errors = {};

    if (!resetForm.password.trim()) {
      errors.password = "Password is required.";
    } else {
      setPasswordStrength(checkPasswordStrength(resetForm.password));
    }

    if (!resetForm.confirmPassword.trim()) {
      errors.confirmPassword = "Confirm Password is required.";
    } else if (resetForm.password !== resetForm.confirmPassword) {
      errors.confirmPassword = "Password does not match. Please check.";
    }

    setResetForm((prev) => ({ ...prev, errors }));
    return Object.keys(errors).length === 0;
  };
  const checkPasswordStrength = (password) => {
    if (password.length < 6) return "Weak";
    if (password.length < 8) return "Fair";
    if (/[A-Z]/.test(password) && /\d/.test(password) && /[!@#$%^&*]/.test(password)) {
      return "Strong";
    }
    return "Fair";
  };
  

  const handleChange = (e) => {
    const { name, value } = e.target;
    setResetForm({
      ...resetForm,
      [name]: value,
      errors: { ...resetForm.errors, [name]: "" },
    });
    if (name === "password") {
      setPasswordStrength(checkPasswordStrength(value));
    }
  };

  const handleResetPassword = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    setLoading(true);
    try {
      const response = await authenticationService.resetPassword({
        email,
        newPassword: resetForm.password,
        verificationCode
      });

      console.log("Reset Password Success:", response);
      setIsResetSuccess(true);
    } catch (err) {
      setResetForm((prev) => ({
        ...prev,
        errors: { apiError: err.response?.data?.message || "Failed to reset password. Please try again." },
      }));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="reset-container">
      <div className="reset-inner-container">
        <div className="signin-image-reset">
          <img src={SignInPageImg} alt="Sign In" />
        </div>
        <div className="reset-content">
          <div className="reset-card">
            <img src={miniLogo} alt="Logo" className="mini-logo" />

            {!showResetForm &&
            
            // !isResetSuccess ? (
            //   <div className="reset-card1">
            //     <h2>Check your Email!</h2>
            //     <p className="email-message">
            //       Thank you! We’ve sent an email with a link to verify your account ownership and reset your password. If you don’t see the email, please check your spam folder.
            //     </p>
            //     <button className="reset-button" onClick={() => setShowResetForm(true)}>
            //       Reset Password
            //     </button>
            //   </div>
            // ) : showResetForm && 
            !isResetSuccess ? (
              <>
                <h2>Reset Password</h2>
                <form onSubmit={handleResetPassword} className="reset-form">
                  <div className="input-group">
                  <InputField
                className={resetForm.errors.password ? "form-input error" : "form-input"}
                    label="New Password"
                    type="password"
                    name="password"
                    placeholder="Enter new password"
                    value={resetForm.password}
                    onChange={handleChange}
                    img={lockIcon}
                  />
               {resetForm.errors.password && <Error message={resetForm.errors.password} type={"error"} />}
               {resetForm.password && (
           
              <div className="password-strength">
              <span style={{ color: "black", fontSize: "14px",fontWeight:"400px"}}>Password Strength: </span>
              <span style={{color:  passwordStrength === "Weak"  ? "red"  : passwordStrength === "Fair"  ? "orange"  : "green",  }} >
                {passwordStrength}
              </span>
               </div>
            )}
          </div>
                <div className="input-group">
                  <InputField
                    className={resetForm.errors.confirmPassword ? "form-input error" : "form-input"}
                    label="Confirm Password"
                    type="password"
                    name="confirmPassword"
                    placeholder="Re-enter new password"
                    value={resetForm.confirmPassword}
                    onChange={handleChange}
                    img={lockIcon}
                  />
                 
                     {resetForm.errors.confirmPassword && <Error message={resetForm.errors.confirmPassword} type={"error"} />}
                     </div>

                  
                    <button type="submit" className="reset-button1">Reset Password</button>
                  <button type="button" className="go-to-home1" onClick={() => navigate("/")}>
                    Go to Home
                  </button>
                </form>
              </>
            ) : (
              <div className="success-message">
                <h2>Password Changed!</h2>
                <p className="success-text">You've successfully completed your password reset!</p>
                <button className="login-button" onClick={() => navigate("/login")}>
                  Login Now
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

 export default ResetPassword;

