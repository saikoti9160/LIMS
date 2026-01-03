import React from 'react';
import "./Error.css";

const Error = ({ message, type }) => {
  let photo = "";

  if (type === "error") {
    photo = "/error.png";
  } else if (type === "warn") {
    photo = "/warnicon.jpg";
  }

  return (
    <div className="errorContainer">
      <img src={photo} alt="Error Icon" className="errorIcon" />
      <span className="errorMessages">{message}</span>
    </div>
  );
};

export default Error;
