import React, { useEffect, useState } from 'react'
import InputField from '../../../components/Homepage/InputField'
import { useLocation, useNavigate } from 'react-router-dom';
import { addExpenseCategory,updateExpenseCategoryById} from '../../../services/LabViewServices/ExpenseCategoryService';
import Swal from '../../Re-usable-components/Swal';
const AddExpenseCategory = () => {
    const navigate = useNavigate();
    const location = useLocation()
    const [viewMode,setViewMode]=useState(false);
    const [editMode,setEditMode]=useState(false);
    const [popup,setPopup]=useState(false)
    const [error,setError] = useState({expenseName:""});
    const [expenseCategory,setExpenseCategory]=useState({
      expenseName: '',
      // labId: location.getItem("userId")
      labId:"3fa85f64-5717-4562-b3fc-2c963f66afa6"
    })
    const userId="3fa85f64-5717-4562-b3fc-2c963f66af77"
    const handleChange=(e)=>{
        setExpenseCategory({...expenseCategory,expenseName : e.target.value})
    }

    const handleSave=async()=>{
      if(!expenseCategory.expenseName){
        setError({expenseName:"Expense name is required"})
        return
      }
      const response= await addExpenseCategory(expenseCategory,userId)
        if (response.statusCode === "200 OK") {
          setPopup({
            icon: "success",
            title: "Saved Successfully",
            onClose: () => {
              navigate("/lab-view/expense-category");
            }
          });
        } else {
          setPopup({
            icon: "delete",
            title: "error",
            onClose: () => { setPopup(null) }
          });
        }
      }
    

    const handleUpdate=async()=>{
      if(!expenseCategory.expenseName){
        setError({expenseName:"Expense name is required"})
        return
      }
      try{
        const response = await updateExpenseCategoryById(expenseCategory.id,expenseCategory)
        if (response.statusCode === "200 OK") {
          setPopup({
            icon: "success",
            title: "Updated Successfully",
            onClose: () => {
              navigate("/lab-view/expense-category");
            }
          });
        } else {
          setPopup({
            icon: "delete",
            title: "error",
            onClose: () => { setPopup(null) }
          });
      }
    }catch(error){
      console.log("error message is :",error)
    }
  }
    
    useEffect(()=>{
      const expenseDetails = location.state?.expenseDetails;
      const mode = location.state?.mode;
      if (mode === 'view') {
        setViewMode(true);
        setExpenseCategory(expenseDetails.data)
      } else if (mode === 'edit') {
        setEditMode(true)
        setExpenseCategory(expenseDetails.data)
      }
    }, [location.state])
  return (
   <div className='expense-category'>
     <div className='title expense-category-title'>
       {viewMode ? "View Expense Category" : editMode ? "Edit Expense Category" : "Add Expense Category"}
     </div>
     <div className='expense-category-content'>
       <div className='expense-category-input'>
       <InputField 
        label="Expense Category"
        onChange={handleChange}
        placeholder="Enter Expense Category"
        name="expenseName"
        value={expenseCategory.expenseName}
        required
        disabled={viewMode}
        error={error.expenseName}
        />
       </div>
     </div>
     <div className='expense-category-buttons'>
        <button className='button-back' onClick={()=>navigate('/lab-view/expense-category')}>Back</button>
        {!viewMode && (editMode ? <button className='btn-primary' onClick={handleUpdate}>Update</button> : <button className='btn-primary' onClick={handleSave}>Save</button>)}
     </div>
     {popup && <Swal {...popup} />}
   </div>
  )
}

export default AddExpenseCategory