import React, { useEffect, useState } from "react";
import "../super-admin-dashboard/super-admin-dashboard.css"
import DropDown from "../../../Re-usable-components/DropDown.jsx"
import DatePicker from "react-datepicker";
import LimsTable from "../../../LimsTable/LimsTable.jsx";
import { Cell, Pie, PieChart } from "recharts";
import { getAllCities, getAllContinents, getAllCountries, getAllStates } from "../../../../services/locationMasterService.js";
import calenderIcon from "../../../../assets/icons/Vector.svg"
import { labManagementGetAll } from "../../../../services/LabManagementService.js";
const SuperAdminDashboard = () => {
    const [dateRange, setDateRange] = useState([null, null]);
    const [startDate, endDate] = dateRange;

    const [continents, setContinents] = useState([]);
    const [countries, setCountries] = useState([]);
    const [states, setStates] = useState([]);
    const [cities, setCities] = useState([]);


    const columns = [
        { key: 'slNo', label: 'Sl. No.' },
        { key: 'labName', label: 'Lab Name' },
        { key: 'createdOn', label: 'Date Added' },
        { key: 'active', label: 'Status ' }
    ]

    const [data, setData] = useState([]);


    const [labSummaryData, setLabSummaryData] = useState({
        totalLabs: 0,
        activeLabs: 0,
        inactiveLabs: 0,
        expiringLabs: 0

    });

    const pieData = [
        { name: "Active Labs", value: labSummaryData.activeLabs, color: "#A7C58A" },
        { name: "Inactive Labs", value: labSummaryData.inactiveLabs, color: "#F6D78B" },
        { name: "Labs with Expiring Plans", value: labSummaryData.expiringLabs, color: "#FDCCA2" }
    ];
    const [request, setRequest] = useState({});

    const handleFilterChange = (field, fieldName) => {
        setRequest((prev) => {
            const value = field.target.value[fieldName];
            const name = field.target.name;
            let updatedRequest = { ...prev, [name]: value };
            if (name === "continent") {
                updatedRequest = { continent: value };
            } else if (name === "country") {
                updatedRequest = { ...prev, country: value }; 
                delete updatedRequest.state;
                delete updatedRequest.city;
            } else if (name === "state") {
                updatedRequest = { ...prev, state: value }; 
                delete updatedRequest.city;
            }
            if (!value) {
                delete updatedRequest[name];
            }
   
            return updatedRequest;
        });
    };
 
    const inputclear = () => {
        setRequest({
        });
        setDateRange([null, null]);

    }
    useEffect(() => {
        const fetchContinents = async () => {
            let response = await getAllContinents();
            setContinents(response.data);
        };
        fetchContinents();
    }, []);

    useEffect(() => {
        const fetchCountries = async () => {
            if (request.continent) {
                let response = await getAllCountries("", [request.continent], 0, 250, "countryName");
                setCountries(response.data);
                setStates([]);
                setCities([]);
            } else {
                setCountries([]);
            }
        };
        fetchCountries();
    }, [request?.continent]);

    useEffect(() => {
        const fetchStates = async () => {
            if (request.country) {
                let response = await getAllStates("", [request.country], 0, 250, "stateName");
                setStates(response.data);
                setCities([]);
            } else {
                setStates([]);
            }
        };
        fetchStates();
    }, [request.country]);

    useEffect(() => {
        const fetchCities = async () => {
            if (request.state) {
                let response = await getAllCities("", [request.state], 0, 250, "cityName");
                setCities(response.data);
            } else {
                setCities([]);
            }
        };
        fetchCities();
    }, [request.state]);
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [totalLabs, setTotalLabs] = useState(0);
    const handleLoadMore = () => { 
        if (data.length < totalLabs) {  
            setCurrentPage((prevPage) => {
                const nextPage = prevPage + 1;
                fetchLabs(nextPage);
                return nextPage;
            });
        }
    };
    const fetchLabs = async (page) => {
        const response = await labManagementGetAll(page, pageSize, request);
        if (response.statusCode === "200 OK") {
            setTotalLabs(response.data.totalLabs);
            setLabSummaryData({
                totalLabs: response.data.totalLabs,
                activeLabs: response.data.activeLabs,
                inactiveLabs: response.data.totalinactivelabs,
                expiringLabs: response.data.expiringLabs
            });
            const formattedData = response.data.lab.map((item) => ({
                ...item,
                createdOn: item.createdOn.split('T')[0],
                active: item.active ? 'Active' : 'Inactive',
            }));
    
            setData((prevData) => [...prevData, ...formattedData]);
        }
    };
    useEffect(() => {
        setData([]);  
        setCurrentPage(0);
        fetchLabs(0);
    }, [request]);  

    const handleDateChange = (field, fieldName) => {
        console.log("field", field);
      
        setRequest((prev) => ({
          ...prev,
          [fieldName]: field,  
        }));
      
        console.log("request", request);
      };

    return (
        <div className="super-admin-container">
            <span className="title">Welcome, Super Admin</span>
            <div className="filteration-div">
                <span className="continent-drp-dwn dropdown-sd">
                    <DropDown
                        options={continents}
                        name="continent"
                        fieldName="continentName"
                        value={request.continent}
                        style={{ height: '35px' }}
                        placeholder="Select Continent"
                        onChange={(selectedOption) => handleFilterChange(selectedOption, "continentName")}
                    />
                </span>
                <span className="country-drp-dwn dropdown-sd">
                    <DropDown
                        options={countries}
                        name="country"
                        placeholder="Select Country"
                        style={{ height: '35px' }}
                        value={request.country}
                        fieldName="countryName"
                        onChange={(selectedOption) => handleFilterChange(selectedOption, "countryName")}
                    />
                </span>
                <span className="state-drp-dwn dropdown-sd">
                    <DropDown
                        placeholder="Select State"
                        options={states}
                        name="state"
                        value={request.state}
                        style={{ height: '35px' }}
                        fieldName={"stateName"}
                        onChange={(selectedOption) => handleFilterChange(selectedOption, "stateName")}
                    />
                </span>
                <span className="city-drp-dwn dropdown-sd">
                    <DropDown
                        placeholder="Select City"
                        value={request.city}
                        options={cities}
                        style={{ height: '35px' }}
                        fieldName={"cityName"}
                        name="city"
                        onChange={(selectedOption) => handleFilterChange(selectedOption, "cityName")} />
                </span>
                <div className='lmdf-date'>
                    <DatePicker
                        selected={startDate}
                        onChange={(update) => {
                            setDateRange(update);
                            handleDateChange(update?.[0] || "","startDate", );
                            handleDateChange(update?.[1] || "","endDate");
                        }}
                        startDate={startDate}
                        endDate={endDate}
                        selectsRange
                        dateFormat="MM/dd/yyyy"
                        placeholderText="Select Date Range"
                     className='custum-datepicker custum-dash'
                    />
                    <img src={calenderIcon} alt='calendar' className='lmdf-calender-icon lmdf-icon' onClick={() => document.querySelector('.custum-datepicker').focus()} />
                </div>
                <span>  <button className="btn-clear" onClick={inputclear}>Clear</button></span>
            </div>
            <div className="count-div">
                <div className="total-lab-count">
                    <span className="span-text">Total Labs</span>
                    <span className="span-data">{labSummaryData.totalLabs}</span>
                </div>
                <div className="total-lab-count active-lab-count">
                    <span className="span-text">Active Labs</span>
                    <span className="span-data" >{labSummaryData.activeLabs}</span>
                </div>
                <div className=" inactive-lab-count">
                    <span className="span-text">Inactive Labs</span>
                    <span className="span-data span-data-count" >{labSummaryData.inactiveLabs}</span>
                </div>
                <div className="inactive-lab-count lab-expiried-count">
                    <span className="span-text">Lab with expiring plans</span>
                    <span className="span-data span-data-count">3,400</span>
                </div>
            </div>
            <div className="summary-div">
                <div className="summary-left-div">
                    <span className="summary-left-div-head">Total Labs Summary</span>
                    <div className="summary-left-div-inner">
                        <PieChart width={200} height={200} className="pie-chart">
                            <Pie
                                data={pieData}
                                dataKey="value"
                                cx="50%"
                                cy="50%"
                                innerRadius={80}
                                outerRadius={100}
                                stroke="white"
                                strokeWidth={2}
                                cornerRadius={10}
                                className="pie"
                            >
                                {pieData.map((entry, index) => (
                                    <Cell key={`cell-${index}`} fill={entry.color} />
                                ))}
                            </Pie>
                        </PieChart>
                        <div className="summary-left-div-bottom">
                            <span className="total-pie-labs">{labSummaryData.totalLabs}</span>
                            <span className="total-pie-text">Total Labs</span>
                        </div>
                    </div>
                    <div className="text-lab-div">
                        {pieData.map((entry, index) => (
                            <div key={index} style={{ display: "flex", alignItems: "center", marginBottom: "5px" }}>
                                <span
                                    style={{
                                        width: "12px",
                                        height: "12px",
                                        backgroundColor: entry.color,
                                        borderRadius: "50%",
                                        marginRight: "8px"
                                    }}
                                />
                                <span className="status-text-lab" >{entry.name}</span>
                            </div>
                        ))}
                    </div>
                </div>
                <div className="summary-right-div">
                    <LimsTable
                        columns={columns}
                        data={data}
                        isPagination={true}
                        onLoadMore={handleLoadMore}
                    />
                </div>
            </div>
        </div>
    )
}
export default SuperAdminDashboard;

