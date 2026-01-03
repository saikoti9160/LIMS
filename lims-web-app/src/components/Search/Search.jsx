import React from 'react'
import searchIcon from "../../assets/icons/search-icon.svg";
import "./Search.css";

export default function Search() {
  return (
    <div>
        <div className='outer-search'>
            <div className="search">
                <input type="text" placeholder="Search..." />
                <img src={searchIcon} alt="" />
            </div>
        </div>
    </div>
  )
}
