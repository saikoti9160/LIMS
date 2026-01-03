import React from 'react';
import './Swal.css';
import closeIcon from '../../assets/icons/add-close.svg';
import addIcon from '../../assets/icons/add-success.svg';
import deleteIcon from '../../assets/icons/delete-icon.svg';
import errorIcon from '../../assets/icons/Error.svg';
import alertIcon from'../../assets/icons/alert.svg';
import Button from './Button';

const Swal = ({ icon, onClose, title,discription="", onButtonClick,isButton=false,buttonText }) => {

    const selectIcon={
        'success' : addIcon,
        'delete' : deleteIcon,
        'alert':  alertIcon,
        'error' : errorIcon
    }


    return (
    <div className="popup-overlay">
      <span className='close-icon'  onClick={onClose}>
      <img
          src={closeIcon}
          alt="Close"
      />
      </span>     
      <div className={isButton? "popup-content-delete" : "popup-content"}>
       <span className={`popup-icon ${icon === 'success' ? 'popup-icon-success' : icon === 'delete' ? 'popup-icon-delete' : icon === 'alert' ? 'popup-icon-alert' : 'popup-icon-error'}`}><img src={selectIcon[icon]} alt="Popup" className="popup-image" /> </span>
        <div className='popup-title'>{title}</div>
        {discription&& <div className='popup-discription'>{discription}</div>}
        {isButton && (
        <button className='btn-primary' onClick={onButtonClick}>{buttonText}</button>
        )}
      </div>
    </div>
  );
};

export default Swal;

