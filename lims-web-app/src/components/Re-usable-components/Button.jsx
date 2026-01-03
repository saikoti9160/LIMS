import React from 'react';
import './Button.css';
const Button = ({ text, onClick }) => {

    const buttonStyle = text?.toLowerCase();

    return (
        <button className={buttonStyle} onClick={onClick || (() => { })}>
            {text}
        </button>
    );
};

export default Button;

