import React, { useState, useEffect, useRef } from 'react';
import './LimsTable.css';
import searchIcon from "../../assets/icons/search-icon.svg";
import viewIcon from "../../assets/icons/eye-icon.svg";
import editIcon from "../../assets/icons/edit-icon.svg";
import deleteIcon from "../../assets/icons/action-delete-icon.svg";
import leftArrow from "../../assets/icons/arrow-left.svg";
import rightArrow from "../../assets/icons/arrow-right.svg";
import initialLeftArrow from "../../assets/icons/initial-left.svg";
import farRightArrow from "../../assets/icons/far-right.svg";
import DropDown from '../Re-usable-components/DropDown';
import contextMenu from '../../assets/images/contextMenu-lg.svg';

const LimsTable = ({
  title = "",
  columns = [],
  data = [],
  totalCount = 0,
  showSearch = false,
  showStatus = false,
  showFilter = false,
  showBranchDropDown = false,
  showClearButton = false,
  showAddButton = false,
  showPagination = false,
  showExportButton = false,
  pageSize: initialPageSize = 10,
  currentPage = 0,
  onClear,
  onAdd,
  onView,
  onFilter,
  onEdit,
  onDelete,
  onExport,
  onPageChange,
  onPageSizeChange,
  onHandleSearch,
  onHandleStatus,
  onInputChange,
  contextMenuHandler,
  searchTerm,
  actionIcons = ["view", "edit", "delete","settleBill"],  
  onButtonAction,
  buttonName,
  onLoadMore,
  buttonTextFromKey,
  dropDownPlaceholder = "Select",
  dropDownOptions = [],
}) => {
  const [searchValue, setSearchValue] = useState('');
  const [pageSize, setPageSize] = useState(initialPageSize);
  const [tableData, setTableData] = useState(data);
  const [contextMenuState, setContextMenuState] = useState({
    visible: false,
    x: 0,
    y: 0,
    selectedRow: null,
  });
  const tableRef = useRef(null);
  
  useEffect(() => {
    if (searchTerm) {
      setSearchValue(searchTerm);
    }
    setTableData(data);
  }, [searchTerm]);
 
  const filteredData = data?.filter(item =>
    Object.values(item).some(value =>
      value?.toString().toLowerCase().includes(searchValue.toLowerCase())
    )
  );

  useEffect(() => {
    if (!onLoadMore) return;

    const handleScroll = () => {
      if (tableRef.current) {
        const { scrollTop, scrollHeight, clientHeight } = tableRef.current;
        if (scrollTop + clientHeight >= scrollHeight - 10) {
          onLoadMore();
        }
      }
    };

    const tableBody = tableRef.current;
    if (tableBody) {
      tableBody.addEventListener("scroll", handleScroll);
    }

    return () => {
      if (tableBody) {
        tableBody.removeEventListener("scroll", handleScroll);
      }
    };
  }, [onLoadMore]);

  const totalPages = Math.ceil(totalCount / pageSize);
  const paginatedData = filteredData;

  
  const handleSearch = (e) => {
    setSearchValue(e.target.value);
    onHandleSearch?.(e.target.value);
  };

  const handleDropDownChange = (selectedValue) => {
    onHandleStatus?.(selectedValue.target.value);
  };

  const handlePageChange = (newPage) => {
    onPageChange?.(newPage);
  };

  const handlePageSizeChange = (e) => {
    const newSize = parseInt(e.target.value);
    setPageSize(newSize);
    onPageSizeChange?.(newSize);
  };

  const handleInputChange = (rowIndex, columnKey, value) => {
    const updatedData = [...tableData];
    updatedData[rowIndex][columnKey] = value;
    setTableData(updatedData);

    if (onInputChange) {
      onInputChange(updatedData[rowIndex], columnKey, value, rowIndex);
    }
  };

  const handleContextMenuClick = (event, row) => {
    event.preventDefault();
    if (contextMenuHandler) {
      contextMenuHandler(event, row);
    }
  };

  // Closes the popup
  const handleCloseMenu = () => {
    setContextMenuState({ visible: false, x: 0, y: 0, selectedRow: null });
  };

  // Close popup when clicking outside
  useEffect(() => {
    const handleOutsideClick = (event) => {
      if (contextMenuState.visible) {
        handleCloseMenu();
      }
    };
    document.addEventListener("click", handleOutsideClick);
    return () => document.removeEventListener("click", handleOutsideClick);
  }, [contextMenuState]);


const renderCell = (row, column, index) => {
  switch (column.key) {
    case 'slNo':
      return (
        <div style={{ textAlign: 'center' }}>
          {currentPage * pageSize + index + 1}
        </div>
      );
    case 'active':
      return (
        <span className={row.active === "Active" ? "status-active" : "status-inactive"}>
          {row.active}
        </span>
      );
    case 'action':
      return (
        <div className='actions-div'>
        <div className="action-imgs-section"
          style={{ 
            textAlign: column.align || 'center'
          }}>
          {actionIcons.includes("view") && (
            <img src={viewIcon} onClick={() => onView?.(row)} alt='view' className="action-icon" />
          )}
          {actionIcons.includes("edit") && (
            <img src={editIcon} onClick={() => onEdit?.(row)} alt='edit' className="action-icon" />
          )}
          {actionIcons.includes("delete") && (
            <img src={deleteIcon} onClick={() => onDelete?.(row)} alt='delete' className="action-icon" />
          )}
        </div>
      </div>
      );
      case 'buttonAction':
      return (
        <div>
        <button
          className="btn-primary buttonAction-div"
          type="button"
          onClick={() => onButtonAction?.(row)}
        >
          {row[buttonTextFromKey] || buttonName}
        </button>
        </div>
      );
      case 'contextMenu':
        return (
          <div className='menu-div'>
            <img
              src={contextMenu}
              alt='contextMenu'
              onClick={(e) => handleContextMenuClick(e, row)}
              className='menu-icon'
            />
          </div>
        );
      default:

        if (column.editable) {
          return (
            <input
              type="text"
              value={row[column.key] || ''}
              onChange={(e) => handleInputChange(index, column.key, e.target.value)}
              placeholder={column.placeholder || 'Enter Here'}
              className="editable-input"
            />
          );
        }
        return column.format ? column.format(row[column.key]) : row[column.key];

    }
  };
  return (
    <div className="lims-table-container">
      <div className='table-title-div'>
        {title && <h2 className="title-text">{title}</h2>}
        <div className={showSearch ? "search-option" : "buttons-section"}>
          {showSearch && (
            <div className="search search-field">
              <input
                type="text"
                placeholder="Search"
                value={searchTerm || searchValue}
                onChange={handleSearch}
                className="search-input"
              />
              <img src={searchIcon} alt="search" />
            </div>
          )}
          <div className="show-buttons">
            {showFilter && <button onClick={onFilter}>Filter</button>}
            {showClearButton && <button className='clear-button' onClick={onClear}>Clear</button>}
            {showStatus &&    <DropDown
                        options={dropDownOptions}
                        name={"active"}
                        onChange={handleDropDownChange}
                      fieldName={"label"}                                         
                    />}
             {showBranchDropDown &&    <DropDown
                        className= "branch-dropdown"
                        options={dropDownOptions}
                        name={"branch"}
                        placeholder='Select Branch'
                        onChange={handleDropDownChange}
                      fieldName={"label"}                                         
                    />}
            {showAddButton && <button onClick={onAdd}>Add</button>}
            {showExportButton && <button onClick={onExport}>Export</button>}
          </div>
        </div>
      </div>
 
      <div className="table-wrapper">
        <table className="table-layout overflow-x-auto" cellSpacing={0}>
          <colgroup>
            {columns?.map((column, index) => (
              <col key={index} style={{ width: column.width || 'auto' }} />
            ))}
          </colgroup>
          <thead className="table-header-row">
            <tr>
              {columns?.map((column, index) => (
                <th
                  key={index}
                  className={`${column.align ? `text-${column.align}` : 'text-center'}`}
                  style={{
                    borderTopLeftRadius: index === 0 ? '8px' : undefined,
                    borderTopRightRadius: index === columns?.length - 1 ? '8px' : undefined,
                  }}
                >
                  {column.label}
                </th>
              ))}
            </tr>
          </thead>
        </table>
        
        <div className='lims-table-body' ref={tableRef}>
          <table className="table-layout" cellSpacing={0}>
            <colgroup>
              {columns?.map((column, index) => (
                <col key={index} style={{ width: column.width || 'auto' }} />
              ))}
            </colgroup>
            <tbody>
              {paginatedData?.map((row, rowIndex) => (
                <tr
                  key={rowIndex}
                  style={{
                    backgroundColor: rowIndex % 2 === 0 ? 'white' : '#FFF3E6'
                  }}
                >
                  {columns?.map((column, colIndex) => (
                    <td
                      key={colIndex}
                      className={`table-cell ${column.editable ? 'editable-cell' : ''}`}
                      style={{
                        borderBottomLeftRadius: rowIndex === paginatedData?.length - 1 && colIndex === 0 ? '8px' : undefined,
                        borderBottomRightRadius: rowIndex === paginatedData?.length - 1 && colIndex === columns?.length - 1 ? '8px' : undefined,
                        borderBottom: rowIndex === paginatedData?.length - 1 ? '1px solid #E1E1E1' : undefined,
                      }}
                    >
                      {renderCell(row, column, rowIndex)}
                    </td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        
        {paginatedData?.length === 0 && (
          <div className="no-records-div">
            <div className="no-records-text">No Records Found</div>
          </div>
        )}
      </div>

      {showPagination && paginatedData?.length > 0 && (
        <div className="pagination-section">
          <div>
            <span className="pagination-text">Results per page: </span>
            <div className="custom-select-wrapper">
              <select
                value={pageSize}
                onChange={handlePageSizeChange}
                className="custom-select results-per-page"
              >
                {[10, 20, 30, 40, 50].map(size => (
                  <option key={size} value={size} className='pagination-options'>
                    {size}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <span className="pagination-text">
            {`${currentPage * pageSize + 1}-${Math.min((currentPage + 1) * pageSize, totalCount)} `}
            <span className="pagination-of">of</span> {` ${totalCount}`}
          </span>
          <div className="pagination-arrows">
            <img
              src={initialLeftArrow}
              onClick={() => handlePageChange(0)}
              disabled={currentPage === 0}
              alt="first page"
            />
            <img
              src={leftArrow}
              onClick={() => handlePageChange(currentPage - 1)}
              disabled={currentPage === 0}
              alt="previous page"
            />
            <img
              src={rightArrow}
              onClick={() => handlePageChange(currentPage + 1)}
              disabled={currentPage >= totalPages - 1}
              alt="next page"
            />
            <img
              src={farRightArrow}
              onClick={() => handlePageChange(totalPages - 1)}
              disabled={currentPage >= totalPages - 1}
              alt="last page"
            />
          </div>
        </div>
      )}
    </div>
  );
};

export default LimsTable;