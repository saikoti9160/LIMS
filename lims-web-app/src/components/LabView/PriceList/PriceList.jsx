import React from "react";
import "./PriceList.css";
import ImageCard from "../../Re-usable-components/ImageCard";
import logo1 from "../../../assets/images/price-list-logo-one.svg";
import logo2 from "../../../assets/images/price-list-logo-two.svg";
import photo1 from "../../../assets/images/price-list-photo-one.svg";
import photo2 from "../../../assets/images/price-list-photo-two.svg";
import { useNavigate } from "react-router-dom";
const PriceList = () => {
  const navigate = useNavigate();
  const handleTestService = () => {
    navigate("/lab-view/price-list/test-service");
  };

  const handleProfileService = () => {
    navigate("/lab-view/price-list/profile-service");
  };

  return (
    <div className="priceListContainer">
      <div className="title price-list-title">Price List</div>
      <div className="price-list-image-container">
        <div className="price-list-first-image">
          <ImageCard
            backgroundImage={photo1}
            logoSrc={logo1}
            serviceName="Test Service"
            onArrowClick={handleTestService}
          />
        </div>
        <div className="price-list-second-image">
          <ImageCard
            backgroundImage={photo2}
            logoSrc={logo2}
            serviceName="Profile Service"
            onArrowClick={handleProfileService}
          />
        </div>
      </div>
    </div>
  );
};

export default PriceList;
