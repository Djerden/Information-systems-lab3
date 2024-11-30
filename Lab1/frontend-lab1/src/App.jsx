import {createBrowserRouter, Navigate, RouterProvider} from "react-router-dom"
import Groups from "./pages/nav_menu/Groups.jsx";
import Root from "./components/Root.jsx";
import ErrorPage from "./pages/ErrorPage.jsx";
import AdminPanel from "./pages/nav_menu/AdminPanel.jsx";
import SignInPage from "./pages/auth/SignInPage.jsx";
import SignUpPage from "./pages/auth/SignUpPage.jsx";
import ProtectedRoute from "./components/security/ProtectedRoute.jsx";

function App() {

    const router = createBrowserRouter([
        {
            path: '/', RouterProvider,
            element: <Root/>,
            errorElement: <ErrorPage/>,
            children: [
                {
                    path: '/',
                    element: <Navigate to="/groups" replace />
                },
                {
                    path: '/groups',
                    element: (
                        <ProtectedRoute>
                            <Groups/>
                        </ProtectedRoute>
                    )
                },
                {
                    path: '/special',
                    element: (
                        <ProtectedRoute>
                            <Groups/>
                        </ProtectedRoute>
                    )
                },
                {
                    path: '/admin',
                    element: (
                        <ProtectedRoute allowedRole={"ROLE_ADMIN"}>
                            <AdminPanel/>
                        </ProtectedRoute>
                    )
                },
                {
                    path: '/sign-in',
                    element: <SignInPage/>
                },
                {
                    path: '/sign-up',
                    element: <SignUpPage/>
                },
            ]
        },
    ])

    return (
        <>
            <RouterProvider router={router}/>
        </>
    )
}

export default App

