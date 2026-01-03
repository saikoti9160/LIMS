import React, { useEffect, useState, useRef } from 'react';
import eyeOpen from '../../assets/icons/eye-open.svg';
import eyeClose from '../../assets/icons/eye-close.svg';
import uploadPhoto from '../../assets/icons/Vector-upload-photo.svg';
import downArrow from "../../assets/icons/down-arrow.svg";
import { fileUploadService } from '../../services/fileUploadService';
import './InputField.css';
import { getAllCountries } from '../../services/locationMasterService';
import { setCountries } from '../../store/slices/locationMasterSlice';
import { useDispatch, useSelector } from 'react-redux';
import closeIcon from '../../assets/icons/add-close.svg';
import Error from '../Re-usable-components/Error';

const InputField = ({ label, type, placeholder, value, onChange, img, name, required, autoComplete, width, readOnly, disabled, handleFileUploaded, existingFileName, checked, phoneCode, error }) => {

  const [phoneNumber, setPhoneNumber] = useState("");
  const [selectedCountry, setSelectedCountry] = useState({ value: "IN", label: "IN", dialCode: "+91" });
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const dispatch = useDispatch();
  const [fileError, setFileError] = useState(null);
  const [isUploading, setIsUploading] = useState(false);
  const [isDragging, setIsDragging] = useState(false);
  const fileInputRef = useRef(null);
  const [localError, setLocalError] = useState(error);

  const MAX_FILE_SIZE = 10 * 1024 * 1024;
  const ALLOWED_TYPES = ['image/jpeg', 'image/jpg', 'image/png', 'image/svg', 'image/svg+xml'];
  const dropdownRef = useRef(null);

  const [showPassword, setShowPassword] = useState(false);
  const countries = useSelector(state => state.locationMaster.countries) || [];

  const countryOptions = countries.map(country => ({
    value: country.countryCode,
    label: country.countryCode,
    dialCode: country.phoneCode
  }));

  const handleShowPassword = () => {
    setShowPassword(!showPassword);
  };

  useEffect(() => {
    if (selectedCountry.dialCode !== phoneCode) {
      const country = countryOptions.find(c => c.dialCode === phoneCode);
      if (country) {
        setSelectedCountry(country);
      }
    }
  }, [countryOptions]);

  const toggleDropdown = () => {
    setIsDropdownOpen(!isDropdownOpen);
  };

  const handleCountryClick = (country) => {
    setSelectedCountry(country);
    setIsDropdownOpen(false);
    onChange({ countryCode: country.dialCode, phoneNumber: phoneNumber });
  };

  const fetchCountries = async () => {
    const { data } = await getAllCountries('', [], 0, 250, 'countryName');
    dispatch(setCountries(data));
  };

  const handlePhoneChange = (e) => {
    const phone = e.target.value;
    setPhoneNumber(phone);
    onChange({ countryCode: selectedCountry.dialCode, phoneNumber: phone });
    if (localError) {
      setLocalError('');
    }
  };

  useEffect(() => {
    if (type === 'phone') {
      fetchCountries();
    }
  }, [dispatch, type]);

  const validateFile = function (file) {
    if (file.size > MAX_FILE_SIZE) {
      throw new Error('File size exceeds 10MB limit');
    }

    if (!ALLOWED_TYPES.includes(file.type)) {
      throw new Error('Only JPEG, JPG, PNG and SVG files are allowed');
    }

    return true;
  }

  const handleChange = (e) => {
    const { value } = e.target;
    onChange(e);
    if (localError && value.trim()) {
      setLocalError('');
    }
  }

  const handleBlur = () => {
    if (required && !value?.trim()) {
      setLocalError(`${label.charAt(0).toUpperCase() + label.slice(1)} is required`);
    } else {
      setLocalError('');
    }
  };

  useEffect(() => {
    setLocalError(error);
  }, [error]);

  const processFile = async (file) => {
    setFileError(null);

    if (file) {
      try {
        validateFile(file);
        setIsUploading(true);
        const destinationKey = ``;
        const uploadResult = await fileUploadService.uploadFile(file, destinationKey);

        if (uploadResult.error) {
          throw new Error(uploadResult.error);
        }

        handleFileUploaded?.(uploadResult);

        if (onChange) {
          onChange({
            target: {
              name: name || 'file',
              value: file,
              files: [file]
            }
          });
        }
      } catch (error) {
        setFileError(error.message);
        if (onChange) {
          onChange({
            target: {
              name: name || 'file',
              value: null,
              files: [],
            }
          });
        }
      } finally {
        setIsUploading(false);
      }
    }
  }

  const renderFileUpload = () => {
    if (existingFileName && (readOnly || disabled)) {
      return (
        <div className={`file-upload-container ${disabled ? 'file-upload-container-disabled' : ''}`}>
          <div className="file-upload-placeholder">
            <span className="existing-filename">{existingFileName}</span>
          </div>
        </div>
      );
    }

    const handleFileUpload = (event) => {
      const file = event.target.files[0];
      if (file) {
        processFile(file);
      }
      if (localError) {
        setLocalError('');
      }
    };

    const handleDragEnter = (e) => {
      e.preventDefault();
      e.stopPropagation();
      if (!disabled && !readOnly) {
        setIsDragging(true);
      }
    };

    const handleDragLeave = (e) => {
      e.preventDefault();
      e.stopPropagation();
      setIsDragging(false);
    };

    const handleDragOver = (e) => {
      e.preventDefault();
      e.stopPropagation();
      if (!disabled && !readOnly) {
        setIsDragging(true);
      }
    };

    const handleDrop = (e) => {
      e.preventDefault();
      e.stopPropagation();
      setIsDragging(false);

      if (disabled || readOnly) return;

      const file = e.dataTransfer.files[0];
      if (file) {
        processFile(file);
      }
    };
    return (
      <div
        className={`file-upload-container ${isDragging ? 'dragging' : ''}`}
        onDragEnter={handleDragEnter}
        onDragLeave={handleDragLeave}
        onDragOver={handleDragOver}
        onDrop={handleDrop}
      >
        <input
          ref={fileInputRef}
          className="file-upload-input"
          type="file"
          accept=".svg, .png, .jpeg, .jpg"
          onChange={handleFileUpload}
          required={required || false}
          name={name}
          disabled={disabled || false}
          readOnly={readOnly || false}
        />
        <div className="file-upload-placeholder">
          {isUploading ? (
            "Uploading..."
          ) : fileError ? (
            <span className="file-error">{fileError}</span>
          ) : (
            <>
              {value?.name || existingFileName ? (
                <div className="uploaded-file-container">
                  <span className="existing-filename">{value?.name || existingFileName}</span>
                  <img src={closeIcon} className="close-button" onClick={() => processFile(null)} alt="Close Icon" />
                </div>
              ) : (
                <>
                  {placeholder || (isDragging ? "Drop file here" : "Drag file here to upload (or) Select File")}
                  {!value && !existingFileName && <img src={uploadPhoto} alt="Upload Icon" />}
                </>
              )}
            </>
          )}
        </div>
      </div>
    );
  };

  const renderLabel = () => {
    if (type !== 'radio') {
      return (
        <label className='input-label'>
          {label}{required && <span className='required'>*</span>}
        </label>
      );
    }
    return null;
  };


  const renderInputField = () => {
    switch (type) {
      case 'checkbox':
        return (
          <label className="checkbox-container">
            <input
              type="checkbox"
              name={name}
              checked={checked}
              onChange={onChange}
              disabled={disabled}
              readOnly={readOnly}
              value={value}
            />
            <span className="custom-checkbox"></span>
            {label}{required && <span className='required'>*</span>}
          </label>
        );

      case 'textarea':
        return (
          <div className="text-area-container">
            <textarea
              className={`input-field-textarea ${disabled ? 'text-area-container-disabled' : ''}`}
              placeholder={placeholder}
              value={value}
              onChange={handleChange}
              onBlur={handleBlur}
              required={required || false}
              disabled={disabled || false}
              name={name}
            />
          </div>
        );

      case 'file':
        return renderFileUpload();

      case 'phone':
        return (
          <div className="phone-number-field">
            <div className={`phone-input-wrapper ${disabled ? 'phone-input-wrapper-disabled' : ''}`}>
              <div className="phone-div">

                <div className="country-dropdown" onClick={!disabled ? toggleDropdown : null}>
                  <span className="selected-country">{selectedCountry.label}</span>
                  <span> <img src={downArrow} className="down-phone-arrow" alt="arrow" /></span>
                </div>

                {isDropdownOpen && (
                  <div className="dropdown-options-phone">
                    {countryOptions.map((country) => (
                      <div
                        key={country.value}
                        className="dropdown-option-phone"
                        onClick={() => !disabled && handleCountryClick(country)}
                      >
                        {country.label}
                      </div>
                    ))}
                  </div>
                )}
                <span className="dial-code">{selectedCountry.dialCode}</span>
              </div>

              <div className="phone-number-input-container">
                <input
                  type="tel"
                  value={value}
                  onChange={handlePhoneChange}
                  onBlur={handleBlur}
                  placeholder="Enter phone number"
                  className="phone-input"
                  name={name}
                  disabled={disabled}
                />
              </div>
            </div>
          </div>
        );

      case 'radio':
        return (
          <label className="radio-container">
            <input
              type="radio"
              name={name}
              value={value}
              checked={checked}
              onChange={onChange}
              disabled={disabled}
            />
            <span className="custom-radio"></span>
            {" "}&nbsp;&nbsp;{label}
          </label>
        );

      case 'password':
        return (
          <div className={`input-field-container ${disabled ? 'input-field-container-disabled' : ''}`} style={{ width: width || 'auto' }}>
            {img && <img src={img} alt="mail-icon" />}
            <input
              className='input-field'
              type={showPassword ? 'text' : 'password'}
              placeholder={placeholder}
              value={value}
              onChange={handleChange}
              onBlur={handleBlur}
              required={required || false}
              name={name}
              readOnly={readOnly || false}
              autoComplete={autoComplete ? 'new-password' : "off"}
              disabled={disabled || false}
            />
            {!showPassword && <img src={eyeClose} alt="eye" onClick={handleShowPassword} className='eye-icon' />}
            {showPassword && <img src={eyeOpen} alt="eye" onClick={handleShowPassword} className='eye-icon' />}
          </div>
        );

      default:
        return (
          <div className={`input-field-container ${disabled ? 'input-field-container-disabled' : ''}`} style={{ width: width || 'auto' }}>
            {img && <img src={img} alt="mail-icon" />}
            <input
              className='input-field'
              type={type}
              placeholder={placeholder}
              value={value}
              onChange={handleChange}
              onBlur={handleBlur}
              required={required || false}
              name={name}
              readOnly={readOnly || false}
              autoComplete={autoComplete ? 'new-password' : "off"}
              disabled={disabled || false}
            />
          </div>
        );
    }
  };

  return (
    <div className='input-fields-root' style={{ width: width || 'auto' }} >
      {renderLabel()}
      {renderInputField()}
      {localError && <Error message={localError} type="error" />}
    </div>
  );
};

export default InputField;