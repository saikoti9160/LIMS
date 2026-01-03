import React, { useState, useRef } from 'react';
import DropDown from '../../Re-usable-components/DropDown';
import './BarCodeManagement.css';
import { useNavigate } from 'react-router-dom';
import closeIcon from '../../../assets/icons/add-close.svg';
import Swal from '../../Re-usable-components/Swal';

function StorageManagement() {
  const navigate = useNavigate();
  const navigateUrl = '/lab-view/barcode-management';
  const [isDataModel, setIsDataModel] = useState(false);
  const rows = 9;
  const cols = 9;
  const [selectedCell, setSelectedCell] = useState(null);
  const [statusMap, setStatusMap] = useState({});
  const previousStatusRef = useRef({});
  const [popupConfig, setPopupConfig] = useState(null);


  const generateGrid = () => {
    return Array.from({ length: rows }, (_, r) => (
      <div key={r + 1} className="storage-row">
        {Array.from({ length: cols }, (_, c) => {
          const key = `R${r + 1}-C${c + 1}`;
          return (
            <div
              key={key}
              className={`storage-cell ${statusMap[key] || 'available'}`}
              onClick={() => handleCellClick(key)}
            >
              <span className="row-part">R{r + 1}-</span>
              <span className="col-part">C{c + 1}</span>
            </div>
          );
        })}
      </div>
    ));
  };

  const handleBack = () => {
    navigate(navigateUrl);
  };

  const handleCellClick = (key) => {
    previousStatusRef.current[key] = statusMap[key] || 'available';

    if (statusMap[key] !== 'occupied') {
      setStatusMap((prev) => ({
        ...prev,
        [key]: 'reserved',
      }));
    }

    setSelectedCell(key);
    setIsDataModel(true);
  };

  const handleClose = () => {
    if (selectedCell) {
      setStatusMap((prev) => ({
        ...prev,
        [selectedCell]: previousStatusRef.current[selectedCell],
      }));
    }
    setIsDataModel(false);
    setSelectedCell(null);
  };

  const handleStore = () => {
    setPopupConfig({
      icon: 'success',
      title: 'Sample data storedSuccessfully',
      onClose: () => {
        setPopupConfig(null);
        setIsDataModel(false);
      },
    })
    if (selectedCell) {
      setStatusMap((prev) => ({
        ...prev,
        [selectedCell]: 'occupied',
      }));
      setIsDataModel(false);
    }
  };

  const handleDelete = () => {
    setIsDataModel(false);
    setPopupConfig({
      icon: 'delete',
      title: 'Are you sure?',
      isButton: true,
      buttonText: 'Delete',
      onButtonClick: () => {
        setStatusMap((prev) => {
          const updatedMap = { ...prev };
          delete updatedMap[selectedCell];
          return updatedMap;
        });

        setPopupConfig({
          icon: 'success',
          title: 'Updated Successfully',
          onClose: () => {
            setPopupConfig(null);
            setIsDataModel(false);
          },
        });
      },
      onClose: () => {
        setPopupConfig(null);
        setIsDataModel(false);
      },
    });
  };

  return (
    <div className="StoreManagement">
      <div className="BardCode-title">Storage Management</div>
      <div className="dropDownStorageContainer">
        <DropDown placeholder="Warehouse" />
        <DropDown placeholder="Rack" />
        <DropDown placeholder="Bay" />
      </div>
      <div className="storageContainer">{generateGrid()}</div>
      {isDataModel && (
        <div className='dataModelContainer'>
          <div className="data-model">
            <div className='closerIcon'><img src={closeIcon} alt='closeIcon' onClick={handleClose} /></div>
            <p>Data Model Content Here</p>
            <div className='btn-group'>
              {statusMap[selectedCell] === 'occupied' ? (
                <button onClick={handleDelete} className='btn-primary'>Delete</button>
              ) : (
                <button onClick={handleStore} className='btn-primary'>Store</button>
              )}
            </div>
          </div>
        </div>
      )}
      {popupConfig && <Swal {...popupConfig} />}
      <div className="button-group">
        <button className="btn-secondary" onClick={handleBack}>Back</button>
      </div>
    </div>

  );
}

export default StorageManagement;
