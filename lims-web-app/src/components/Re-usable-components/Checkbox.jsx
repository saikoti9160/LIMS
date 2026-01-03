
import React from "react";
import "./Checkbox.css";

const Checkbox = ({
  onChange = () => {},
  disabled = false,
  checked = false,
  borderColor = "#fb8500",
  checkedBorderColor = "#fb8500",
  checkmarkColor = "#fb8500",
}) => {
  const handleChange = (e) => {
    if (!disabled) {
      onChange(e.target.checked);
    }
  };

  return (
    <div className="checkboxContainer">
      {!checked ? (
        <input
          type="checkbox"
          className="customCheckbox"
          onChange={handleChange}
          disabled={disabled}
          style={{
            borderColor: borderColor,
          }}
        />
      ) : (
        <img
          src={require("../../assets/icons/checkBox.svg").default}
          className="checkIcon"
          alt="tick"
          onClick={() => !disabled && onChange(false)} 
        />
      )}
    </div>
  );
};

export default Checkbox;
