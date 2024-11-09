import Header from "./Header.jsx";
import {Outlet} from "react-router-dom";

export default function Root() {
    return (
        <>
            <Header />
            <Outlet />
        </>
    );
}