import React, { useState } from "react";
import "./Sidebar.css";
import { useNavigate } from "react-router-dom";
import { items } from "./SidebarModel";
import downArrow from "../../assets/icons/down-arrow.svg";

const Sidebar = () => {
  const navigate = useNavigate();

  const [activeMenu, setActiveMenu] = useState(new Set(["Dashboard"]));
  const [menuItems, setMenuItems] = useState(items);

  const objects = [
    "Dashboard",
    "Masters",
    "Staff Management",
    "Lab Management",
    "Packages",
    "Notification",
    "Support Tickets",
  ];

  const onClickActions = (item) => {
    if (!menuItems[item]) return;

    let itemsToRemove = [];

    activeMenu.forEach((activeItem) => {
      if (menuItems[activeItem]?.level >= menuItems[item]?.level) {
        itemsToRemove.push(activeItem);
        handleMenuItems(activeItem, false);
      }
    });

    setActiveMenu((prevActiveMenu) => {
      const newActiveMenu = new Set(prevActiveMenu);
      itemsToRemove.forEach((k) => newActiveMenu.delete(k));
      newActiveMenu.add(item);
      return newActiveMenu;
    });

    handleMenuItems(item, true);

    if (menuItems[item]?.link) {
      navigate(menuItems[item].link);
    }
  };

  const handleMenuItems = (item, toggle) => {
    setMenuItems((prevItems) => ({
      ...prevItems,
      [item]: {
        ...prevItems[item],
        isActive: toggle,
      },
    }));
  };

  const renderSubItems = (itemName) => {
    if (!menuItems[itemName]?.subItems) return null;

    return (
      <div
        className="sub-items"
        style={{ 
          backgroundColor: menuItems[itemName]?.subItems && menuItems[itemName]?.level === 0 ? "rgba(255, 243, 230, 0.35)" : ""
        }}
      >
        {menuItems[itemName]?.subItems?.map((item) => (
          <div
            key={item}
            style={{
              display: "flex",
              flexDirection: "column",
              paddingLeft: `${menuItems[item]?.level * 20 + 10}px`,
              gap: "10px",
            }}
          >
            <span
              style={{
                display: "flex",
                gap: "10px",
                flexDirection: "row",
                color: activeMenu.has(item) ? "rgba(251, 133, 0, 1)" : "",
              }}
            >
              {
                menuItems[item]?.icon &&
                <span className="sidebar-img-container">
                <img src={menuItems[item]?.icon} alt="sidebar-icon" />
              </span>
              }
              <span
                className="sidebar-item"
                onClick={() => onClickActions(item)}
              >
                {menuItems[item]?.name}
                {menuItems[item]?.subItems && (
                  <span className="sidebar-img-container">
                    <img
                      src={downArrow}
                      alt="down-arrow-icon"
                      className={`${
                        menuItems[item]?.isActive ? "down-arrow-location-master" : "down-arrow"
                      }`}
                    />
                  </span>
                )}
              </span>
            </span>
            {menuItems[item]?.subItems && menuItems[item]?.isActive && renderSubItems(item)}
          </div>
        ))}
      </div>
    );
  };

  return (
    <div className="sidebar-container">
      <div className="sidebar">
        {objects.map((item) =>
          menuItems[item]?.isPermitted ? (
            <div key={item}>
              <div
                className={`sidebar-inner ${
                  activeMenu.has(item) && menuItems[item]?.level === 0
                    ? "selected-item"
                    : ""
                }`}
              >
                <span className="sidebar-img-container">
                  <img src={menuItems[item]?.icon} alt="sidebar-icon" />
                </span>
                <span className="sidebar-item" onClick={() => onClickActions(item)}>
                  {menuItems[item]?.name}
                </span>
                {menuItems[item]?.subItems && (
                  <span className="sidebar-img-container">
                    <img
                      src={downArrow}
                      alt="down-arrow-icon"
                      className={`${
                        menuItems[item]?.isActive
                          ? "down-arrow-location-master"
                          : "down-arrow"
                      }`}
                    />
                  </span>
                )}
              </div>
              <div className="sub-items-root">
                {menuItems[item]?.subItems && menuItems[item]?.isActive && renderSubItems(item)}
              </div>
            </div>
          ) : null
        )}
      </div>
    </div>
  );
};

export default Sidebar;
