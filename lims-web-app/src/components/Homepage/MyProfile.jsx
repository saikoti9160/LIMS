import React, { useState } from "react";
import "./MyProfile.css";
import InputField from "./InputField";
import EditBtnPencil from "../../assets/icons/edit-btn-pencil.svg";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";

const MyProfile = ({ showTitle = true }) => {
  const userDetails = useSelector((state) => state?.user?.user);
  const navigate = useNavigate();

  const handleEdit = (category) => {
    if (category === 'profile') {
      navigate('/my-profile/edit', {
        state: {
          mode: 'profile',
        }
      });
    } else {
      navigate('/my-profile/edit', {
        state: {
          mode: 'password',
        }
      });
    }
  };

  return (
    <div className="profile-container">
    {showTitle &&   <h2 className="title">My Profile</h2> }

      {/* Profile Section */}
      <div className="card">
        <div className="profile">
          <img
            src={userDetails?.profilePic}
            alt="Profile"
            className="profile-pic"
          />
          <div className="profile-info">
            <h3>{userDetails?.userName}</h3>
            <p>{userDetails?.email}</p>
          </div>
          <div className="edit-btn-container">
            <button className="edit-btn" onClick={() => handleEdit('profile')}>
              Edit <span className="edit-icon"><img src={EditBtnPencil} alt="Edit-icon" /></span>
            </button>
          </div>
        </div>
      </div>

      <div className="card">
        <div className="password-container">
          <h3 className="profile-section-title title">Password</h3>
          <InputField
            label="Password"
            type="password"
            placeholder="********"
            autoComplete="true"
            width='470px'
          />
        </div>
        <div className="edit-btn-container">
          <button className="edit-btn" onClick={() => handleEdit('password')}>
            Edit <span className="edit-icon"> <img src={EditBtnPencil} alt="Edit-icon" /> </span>
          </button>
        </div>
      </div>
    </div>
  );
};

export default MyProfile;
