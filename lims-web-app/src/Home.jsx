import { useSelector } from "react-redux";
import Layout from "./Layout"
import Sidebar from "./components/Sidebar/Sidebar"

const Home = () =>{

    return (
        <div>
            <div>
                {/* <Header /> */}
            </div>
            <div className='content-wrapper'>
                <div className='app-sidebar'>
                    <Sidebar />
                </div>
                <div className='main-content'>
                  <Layout />
                </div>
            </div>
        </div>
    )
}

export default Home