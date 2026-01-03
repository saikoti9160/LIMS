import React, { useState, useEffect } from 'react';
import LimsTable from '../../LimsTable/LimsTable';
import './Doctor.css';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { getAllDoctors, getDoctorById, updateDoctorById, deleteDoctorById } from "../../../services/LabViewServices/DoctorService";
import { getLabDepartments } from '../../../services/LabViewServices/labDepartmentService';
import Swal from '../../Re-usable-components/Swal';

function Doctor() {
    const navigate = useNavigate();
    const navigateUrl = '/doctor/master/add'
    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [createdBy, setCreatedBy] = useState('519c5fba-c91a-40b2-9541-44aa60dd0293');
    const [popupConfig, setPopupConfig] = useState(null);
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);

    const columns = [
        { key: 'slNo', label: 'Sl. No.', width: '100px', align: 'center' },
        { key: 'doctorSequenceId', label: 'Doctor ID', width: '1fr', align: 'center' },
        { key: 'doctorName', label: 'Doctor Name', width: '1fr', align: 'center' },
        { key: 'departmentName', label: 'Department', width: '1fr', align: 'center' },
        { key: 'email', label: 'Email ID', width: '1fr', align: 'center' },
        { key: "action", label: "Action", width: "116px" },
    ];


    const handleAdd = () => {
        navigate(navigateUrl, { state: { mode: 'add' } });
    };
    const handleView = async (row) => {
        const response = await getDoctorById(row.id);
        const doctorDetails = response.data;
        navigate(navigateUrl, {
            state: {
                mode: 'view',
                doctorData: doctorDetails,
            },
        });
    };
    const handleEdit = async (row) => {
        const response = await getDoctorById(row.id);
        const doctorDetails = response.data;
        navigate(navigateUrl, {
            state: {
                mode: 'edit',
                doctorData: doctorDetails,
            },
        });
    };

    const handleDelete = async (row) => {
        setPopupConfig({
            icon: 'delete',
            title: 'Are you sure?',
            text: 'Do you want to delete this Doctor?',
            onButtonClick: async () => {
                const response = await deleteDoctorById(row.id);
                if (response.statusCode === "200 OK") {
                    setPopupConfig({
                        icon: 'success',
                        title: 'Deleted  Successfully',
                        onClose: () => {
                            setPopupConfig(null);
                            fetchData();
                        },
                    })
                }
            },
            onClose: () => setPopupConfig(null)
        })
    };
    const [labDepartments, setLabDepartments] = useState([]);
    const [doctors, setDoctors] = useState([]);

    const getLabDepartmentslist = async () => {
        const response = await getLabDepartments(0, 10, '', createdBy);
        setLabDepartments(response.data.content);
    };
    const getDepartmentName = (departmentId) => {
        const department = labDepartments.find(dept => dept.id === departmentId);
        return department ? department.name : 'Unknown';
    };

    const doctorsWithDepartmentNames = doctors.map(doctor => ({
        ...doctor,
        departmentName: getDepartmentName(doctor.departmentId)
    }));

    useEffect(() => {
        fetchData();
    }, []);



    const fetchData = async () => {
        try {
            await getLabDepartmentslist();

            const response = await getAllDoctors(createdBy, " ", true, currentPage, pageSize);

            if (response.statusCode === "200 OK") {
                const transformedData = response.data.map((user, index) => ({
                    id: user.id,
                    slNo: currentPage * pageSize + index + 1,
                    doctorSequenceId: user.doctorSequenceId,
                    doctorName: user.doctorName,
                    email: user.email,
                    departmentName: user.departmentName,
                }));
                setData(transformedData);
            } else {
                setError(new Error("Failed to fetch data"));
            }
        } catch (error) {
            setError(error);
        } finally {
            setLoading(false);
        }
    };



    return (
        <div className='DoctorMaster'>
            <LimsTable
                title="Doctor Master"
                columns={columns}
                data={data}
                onAdd={handleAdd}
                onView={handleView}
                onEdit={handleEdit}
                onDelete={handleDelete}
                showAddButton
                showSearch
                showPagination
            />
            {popupConfig && (
                <Swal
                    icon={popupConfig.icon}
                    title={popupConfig.title}
                    text={popupConfig.text}
                    onButtonClick={popupConfig.onButtonClick}
                    onClose={popupConfig.onClose}
                />
            )}
        </div>
    );
}

export default Doctor;
