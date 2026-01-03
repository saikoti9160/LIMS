import React from 'react'
import arrow from "../../assets/images/image-card-arrow.svg"
import "./ImageCard.css"
const ImageCard = ({
    backgroundImage,
    logoSrc,
    serviceName,
    onArrowClick,
}) => {
    return (
        <div className='priceListContainer'>
            <div className="service-card">
                <div
                    className="service-card-background"
                    style={{ backgroundImage: `url(${backgroundImage})` }}
                />
                <div className="service-card-content">
                    <div className="service-logo">
                        <img src={logoSrc} alt='logo' />
                </div>
                <div className="service-name">{serviceName}</div>
                <button
                        onClick={onArrowClick}
                        className="arrow-button"
                        aria-label="View service details"
                >
                <img src={arrow} alt="Service arrow" />
                </button>
                <>
                    <div className="decorative-circle top-left" />
                    <div className="decorative-circle bottom-right"/>
                </>
                </div>
            </div>
        </div>
    )
}

export default ImageCard