import React, { useEffect, useState } from "react";
import "./ExpenseCategory.css";
import LimsTable from "../../LimsTable/LimsTable";
import { useNavigate } from "react-router-dom";
import { ExpenseCategoryGetAll, getExpenseCategoryById, deleteExpenseCategoryById } from "../../../services/LabViewServices/ExpenseCategoryService";
import Swal from "../../Re-usable-components/Swal";
const ExpenseCategory = () => {
    const [expenseDetails, setExpenseDetails] = useState([]);
    const [popup, setPopup] = useState(false);
    const navigate = useNavigate();
    const columns = [
        { key: "slNo", label: "Sl. No.", width: "100px", align: "center" },
        { key: "expenseName", label: "Expense Name", width: "1fr", align: "center", },
        { key: "action", label: "Action", width: "116px", height: "40px", align: "center", },
    ];

    // const id= localStorage.getItem('userId');
    const userId = "3fa85f64-5717-4562-b3fc-2c963f66afa6";

    const handleAdd = () => {
        navigate("/lab-view/expense-category/add");
    };
    const fetchExpense = async (id) => {
        try {
            const response = await ExpenseCategoryGetAll(id);
            setExpenseDetails(response.data);
        } catch {
            console.log("error fetching expense details");
        }
    };

    const handleView = async (row) => {
        try {
            const expenseDetails = await getExpenseCategoryById(row.id);
            navigate("/lab-view/expense-category/add", {
                state: {
                    expenseDetails,
                    mode: "view",
                },
            });
        } catch (error) {
            console.error("Error fetching expense-category details:", error);
        }
    };

    const handleEdit = async (row) => {
        try {
            const expenseDetails = await getExpenseCategoryById(row.id);
            navigate("/lab-view/expense-category/add", {
                state: {
                    expenseDetails,
                    mode: "edit",
                },
            });
        } catch (error) {
            console.error("Error fetching expense-category details:", error);
        }
    };

    const handleDelete = async (row) => {
        setPopup({
            icon: "delete",
            title: "Are you sure?",
            isButton: true,
            buttonText: "Delete",
            onButtonClick: () => handleDeleteConfirm(row),
            onClose: handleDeleteClose,
        });
    };

    const handleDeleteClose = () => {
        setPopup(false);
    };

    const handleDeleteConfirm = async (row) => {
        const response = await deleteExpenseCategoryById(row.id);
        if (response?.statusCode === "200 OK") {
            setPopup({
                icon: "success",
                title: "Deleted Successfully",
                onClose: handleDeleteClose,
            });
            fetchExpense(userId);
        } else {
            setPopup({
                icon: "error",
                title: "error deleting lab",
                onClose: handleDeleteClose,
            });
        }
    };

    useEffect(() => {
        fetchExpense(userId);
    }, []);
    return (
        <div className="expense-category">
            <div className="title expense-category-title">Expense Category</div>
            <div className="expense-category-table">
                <LimsTable
                    columns={columns}
                    data={expenseDetails}
                    onAdd={handleAdd}
                    onView={handleView}
                    onEdit={handleEdit}
                    onDelete={handleDelete}
                    showAddButton
                    showSearch
                />
            </div>
            {popup && <Swal {...popup} />}
        </div>
    );
};

export default ExpenseCategory;
