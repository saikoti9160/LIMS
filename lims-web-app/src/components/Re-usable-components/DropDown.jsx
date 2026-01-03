import React, { useState, useRef, useEffect } from "react";
import "./DropDown.css";
import downArrow from "../../assets/icons/down-arrow.svg";
import closeIcon from "../../assets/icons/add-close.svg";
import Error from '../Re-usable-components/Error';
const DropDown = ({
  options = [],
  placeholder = "Select",
  onChange,
  label,
  name,
  style,
  required,
  multiple = false,
  value,
  disabled = false,
  fieldName,
  width,
  error
}) => {
  // console.log('from dropdown ', options, fieldName);
  
  const [isOpen, setIsOpen] = useState(false);
  const [selectedOptions, setSelectedOptions] = useState([]);
  const [filterText, setFilterText] = useState("");
  const dropdownRef = useRef(null);
  const [hoveredIndex, setHoveredIndex] = useState(-1);
  const optionRefs = useRef([]);
  const[localError,setLocalError]=useState(error);

  const filteredOptions = (Array.isArray(options) ? options : []).filter(option =>
    option[fieldName]?.toLowerCase().includes(filterText.toLowerCase())
  );


  useEffect(() => {
    if (!value) {
      setSelectedOptions([]);
    } else if (Array.isArray(value)) {
      setSelectedOptions(value.map(v => (typeof v === "string" ? { value: v } : v)));
    } else {
      setSelectedOptions([typeof value === "string" ? { value } : value]);
    }
  }, [value]);


  useEffect(() => {
    const handleOutsideClick = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsOpen(false);
        setFilterText("");
      }
    };
    document.addEventListener("mousedown", handleOutsideClick);
    return () => document.removeEventListener("mousedown", handleOutsideClick);
  }, []);

  useEffect(() => {
    const handleKeyUp = (event) => {
      if (isOpen && event.key !== "Backspace" && !["Shift", "CapsLock", "Escape", "Tab", "Enter", "ArrowUp", "ArrowDown", "ArrowLeft", "ArrowRight", "Control", "Alt", "Meta", "Delete"].includes(event.key)) {
        setFilterText((prevText) => prevText + event.key);
      } else if (isOpen && event.key === "Backspace") {
        setFilterText((prevText) => prevText.slice(0, -1));
      }
    };
    document.addEventListener("keyup", handleKeyUp);
    return () => document.removeEventListener("keyup", handleKeyUp);
  }, [isOpen, filterText]);

  useEffect(() => {
    const handleKeyDown = (event) => {
      if (!isOpen) return;

      if (["Shift", "CapsLock", "Escape", "Tab", "Control", "Alt", "Meta"].includes(event.key)) {
        return;
      }

      if (event.key === "ArrowDown") {
        event.preventDefault();
        setHoveredIndex((prev) => {
          const newIndex = prev < filteredOptions.length - 1 ? prev + 1 : 0;
          optionRefs.current[newIndex]?.scrollIntoView({ behavior: "smooth", block: "nearest" });
          return newIndex;
        });
      } else if (event.key === "ArrowUp") {
        event.preventDefault();
        setHoveredIndex((prev) => {
          const newIndex = prev > 0 ? prev - 1 : filteredOptions.length - 1;
          optionRefs.current[newIndex]?.scrollIntoView({ behavior: "smooth", block: "nearest" });
          return newIndex;
        });
      } else if (event.key === "Enter" && hoveredIndex !== -1) {
        event.preventDefault();
        handleOptionClick(filteredOptions[hoveredIndex]);
        setHoveredIndex(-1);
        setIsOpen(false);
        setFilterText("");
      } else if (event.key === "Backspace") {
        if (filterText === "" && selectedOptions.length > 0) {
          const newSelectedOptions = [...selectedOptions];
          newSelectedOptions.pop(); 
          setSelectedOptions(newSelectedOptions);
          if (onChange) onChange({ target: { name, value: newSelectedOptions } });
        } 
      }
    };

    document.addEventListener("keydown", handleKeyDown);
    return () => document.removeEventListener("keydown", handleKeyDown);
  }, [isOpen, hoveredIndex, filteredOptions]);



  const toggleDropdown = () => {
   if(!disabled){
   setIsOpen((prev) => !prev);
   }
    setFilterText("");
  };

  useEffect(() => {
    setLocalError(error);  // Sync with parent error state
}, [error]);
const handleBlur = () => {
 
    if (required && selectedOptions.length === 0) {
      setLocalError(`${label} is required`);
    } else {
      setLocalError("");
    }

};

  const handleOptionClick = (option) => {
    const normalizedOption = typeof option === "string" ? { value: option, label: option } : option;
    setSelectedOptions((prevSelected) => {
      let newSelected;
      if (multiple) {
        const isAlreadySelected = prevSelected.some((o) => o[fieldName] === normalizedOption[fieldName]);
        newSelected = isAlreadySelected
          ? prevSelected.filter((o) => o[fieldName] !== normalizedOption[fieldName])
          : [...prevSelected, normalizedOption];
  
        if (newSelected.length > 0) {
          setLocalError("");
        }
      } else {
        newSelected = [normalizedOption];
        setTimeout(() => {
          setSelectedOptions(newSelected);
          if (onChange) {
            onChange({ target: { name, value: newSelected[0] } });
          }
        }, 0);
        setIsOpen(false); 
        setLocalError(""); 
        setFilterText(""); 
        return prevSelected;
      }
  
      if (onChange) {
        onChange({ target: { name, value: newSelected } });
      }
  
      return newSelected;
    });
  };
  
  
  
  const handleRemoveChip = (option, event) => {
    event.stopPropagation();
    const newSelectedOptions = selectedOptions.filter((o) => o[fieldName] !== option[fieldName]);
    setSelectedOptions(newSelectedOptions);
    if (onChange) {
      onChange({ target: { name, value: newSelectedOptions } });
    }
  };

  const isSelected = (option) => {
    return multiple
      ? selectedOptions.some((o) => o[fieldName] === option[fieldName])
      : selectedOptions[0]?.[fieldName] === option[fieldName];
  };



  return (
    <div style={{ width: width || 'auto' }} className="dynamic-dropdown">
      {label && (
        <label className="input-label">
          {label}{required && <span className="required">*</span>}
        </label>
      )}
      <div className="dropdown-container" style={style} ref={dropdownRef}>
        <div className={`dropdown-header ${disabled ? 'disabled-dropdown-header' : ''}`} onClick={toggleDropdown} onBlur={handleBlur} tabIndex={0} >
          {multiple ? (
            <div className="selected-chips">
              {selectedOptions.length > 0 ? (
                selectedOptions.map((option, index) => (
                  <span key={index} className="chip">
                      {option[fieldName]} 
                    {!disabled && (
                      <img
                        src={closeIcon}
                        alt="remove"
                        className="chip-close"
                        onClick={(e) => handleRemoveChip(option, e)}
                        onBlur={handleBlur}
                      />
                    )}
                  </span>
                ))
              ) : (
                <span className="placeholder">
                  {filterText || placeholder}
                </span>
              )}
            </div>
          ) : (
            <span className="placeholder">
              {filterText ||
                (selectedOptions.length > 0
                  ? <span className="dropdown-selected-option">{selectedOptions[0]?.[fieldName] || selectedOptions[0]?.value}</span>
                  : <span className="dropdown-placeholder">{placeholder}</span>)}
            </span>
          )}
          <span className={`dropdown-arrow ${isOpen ? "open" : ""}`}>
            <img src={downArrow} className="arrow" alt="arrow" />
          </span>
        </div>
        {!disabled && isOpen && (
          <div className="dropdown-options">
            {filteredOptions.length > 0 ? (
              filteredOptions.map((option, index) => (
                <div
                  key={index}
                  ref={(el) => (optionRefs.current[index] = el)}
                  className={`dropdown-option 
                  ${isSelected(option) ? "selected" : ""} 
                  ${index === hoveredIndex ? "hovered" : ""}`}
                  onMouseEnter={() => setHoveredIndex(index)}
                  onClick={() => handleOptionClick(option)}
                >
                  {multiple && (
                    <input
                      type="checkbox"
                      checked={isSelected(option)}
                      onChange={() => handleOptionClick(option)}
                      className="dropdown-checkbox"
                    />
                  )}
                  {option[fieldName] || option.value}
                </div>
              ))
            ) : (
              <div className="dropdown-option no-options">No results found</div>
            )}
          </div>
        )}
      </div>
      {localError && <Error message={localError} type="error" />}
    </div>
  );
};

export default DropDown;