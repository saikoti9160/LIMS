import React, { useEffect, useState } from 'react'
import LimsTable from '../../LimsTable/LimsTable'
import { useNavigate } from 'react-router-dom';
import { deleteExpenseById, getAllExpenses, getExpenseById } from '../../../services/LabViewServices/ExpenseMasterService';
import Swal from '../../Re-usable-components/Swal';

const ExpenseMaster = () => {
    const column = [
        { key: 'slNo', label: 'Sl. No.', width: '100px', align: 'center' },
        { key: 'ExpenseDate', label: 'Expense Date', width: '1fr', align: 'center' },
        { key: 'ExpenseAmount', label: 'Expense Amount (INR)', width: '1fr', align: 'center' },
        { key: 'Category', label: 'Category', width: '1fr', align: 'center' },
        { key: 'action', label: 'Action', width: '116px', height: '40px', align: 'center' }
    ];
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [totalCount, setTotalCount] = useState(0);
    const navigate = useNavigate()
    const [expenseMaster, setExpenseMaster] = useState([]);
    const [selectedRow, setSelectedRow] = useState(null);
    const [showPopup, setShowPopup] = useState(false);
    const [successPopup, setSuccessPopup] = useState(false);
    const[searchText,setSearchText]=useState('');

    const labId = "3fa85f64-5717-4562-b3fc-2c963f66afa6"; // Replace with actual Lab ID

    const fetchExpenseMaster = async (page, size) => {
        try {
            const response = await getAllExpenses(labId,searchText, page, size); // Ensure you send the correct params
            // console.log('API Response:', response.data); // Log the response

            if (response.statusCode === "200 OK") {
                // console.log("response", response.data)
                const transformedData = response.data.map((expense, index) => ({
                    slNo: page * size + index + 1,
                    ExpenseDate: expense.expenseDate || 'N/A',
                    ExpenseAmount: expense.expenseAmount || 'N/A',
                    Category: expense.expenseCategory?.expenseName || 'N/A',
                    action: 'Actions',
                    fullObject: expense
                }));
                setExpenseMaster(transformedData);
                setTotalCount(response.totalCount);
            } else {
                console.error('Failed to fetch expenses: ', response);
            }
        } catch (error) {
            console.error('Error fetching expenses:', error);
        }
    };

    const handleExpenseAdd = () => {
        navigate("/add-expense-master")
    }
    const handleExpenseView = async (row) => {
        try {
            const expenseDetails = await getExpenseById(row.fullObject.id); // Fetch details using ID
            navigate('/add-expense-master', {
                state: {
                    expenseDetails,
                    mode: 'view', // Set mode to "view"
                },
            });
        } catch (error) {
            console.error('Error fetching expense details:', error);
        }
    };
    
    const handleExpenseEdit = async(row) => {
        // console.log("expenes master edit")
        try {
          const expenseDetails = await getExpenseById(row.fullObject.id);
        //   console.log("expenseDetails edit mode", expenseDetails)
          navigate('/add-expense-master', {
            state: {
            expenseDetails,
              mode: 'edit',
            },
          });
        } catch (error) {
          console.error('Error fetching expense details:', error);
        }
    }
    const handleDelete = (row) => {
        setSelectedRow(row.fullObject);
        setShowPopup(true);
    }
    const confirmDelete = async () => {
        if (!selectedRow) return;
        try {
        //   console.log("Deleting user with ID:", selectedRow.id);
          await deleteExpenseById(selectedRow.id);
          setExpenseMaster((prev) =>
            prev.filter((expense) => expense.fullObject.id !== selectedRow.id)
          );
          setShowPopup(false);
          setSuccessPopup(true)
        } catch (error) {
          console.error("Error deleting department:", error);
          setShowPopup(false);
        }
    };

    useEffect(() => {
        fetchExpenseMaster(currentPage, pageSize);
    }, [currentPage, pageSize,searchText]);
    const handlePageChange = (newPage) => {
        if (newPage >= 0 && newPage < Math.ceil(totalCount / pageSize)) {
            setCurrentPage(newPage);
        }
        // console.log("usermaster", expenseMaster)
    };
    const handlePageSizeChange = (newSize) => {
        setPageSize(newSize);
        setCurrentPage(0);
    };
    const closePopup = () => {
        setShowPopup(false);
      };
     
      const closeSuccessPopup = () => {
        setSuccessPopup(false);
        fetchExpenseMaster(currentPage, pageSize);
      };
      
      const handleSearch = (event) => {
        setSearchText(event);
        setCurrentPage(0);
      };

    return (
        <div>

            <LimsTable
                title="Expense Master"
                columns={column}
                data={expenseMaster}
                totalCount={totalCount} // Pass the total count to LimsTable
                currentPage={currentPage}
                pageSize={pageSize}
                onAdd={handleExpenseAdd}
                onView={handleExpenseView}
                onEdit={handleExpenseEdit}
                onDelete={handleDelete}
                showAddButton
                showClearButton={false}
                showExportButton={false}
                showSearch
                showPagination
                onPageChange={handlePageChange}
                onPageSizeChange={handlePageSizeChange}
                onHandleSearch={handleSearch}
            />
            {/* Swal Popup */}
            {showPopup && (
                <Swal
                    icon="delete"
                    title="Are you sure?"
                    text
                    onClose={closePopup}
                    onButtonClick={confirmDelete}
                />

            )}
            {/* Success Swal Popup */}
            {successPopup && (
                <Swal
                    icon="success"
                    title="Deleted Successfully"
                    onClose={closeSuccessPopup}
                />
            )}
        </div>
    )
}

export default ExpenseMaster
