import { Route, Routes } from "react-router-dom";
import SignInPage from "./components/Authentication/SignInPage";
import SignUpPage from "./components/Authentication/SignUpPage";

const Authentication = () => {

    return (
        <Routes>
          <Route path="login" element={<SignInPage />} />
          <Route path="sign-up" element={<SignUpPage />} />
        </Routes>
    )

}

export default Authentication;