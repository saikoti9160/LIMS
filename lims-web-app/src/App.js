import './App.css';
import Header from './components/Homepage/Header';
import Home from './Home';
import { Route, Routes, useNavigate } from 'react-router-dom';
import SignInPage from './components/Authentication/SignInPage';
import SignUpPage from './components/Authentication/SignUpPage';
import { useState } from 'react';
import { useDispatch } from 'react-redux';
import { setUser } from './store/slices/userSlice';

function App() {

  const [isAuthenticated, setIsAuthenticated] = useState(localStorage.getItem('isAuthenticated') === 'true');
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const userDetails = JSON.parse(localStorage.getItem('userDetails'));
  dispatch(setUser(userDetails))
  
  const handleOnChange = (componentName) => {
    if(componentName === 'header') {
      localStorage.setItem('isAuthenticated', false);
      setIsAuthenticated(false);
      navigate('/login');
    }
    else if(componentName === 'signin') {
      localStorage.setItem('isAuthenticated', true);
      setIsAuthenticated(true);
      navigate('/dashboard');
    }
  }
    
  return ( 
    <div className="app-container">
        <Header onChange={handleOnChange} isLogin={isAuthenticated} />
        {
          !isAuthenticated && (
            <Routes>
              <Route path="*" element={<SignInPage onChange={handleOnChange} />} />
              <Route path="login" element={<SignInPage onChange={handleOnChange} />} />
              <Route path="sign-up" element={<SignUpPage />} />
            </Routes>
          )
        }
        {
          isAuthenticated && <Home />
        }
    </div>  
  );
}

export default App;
