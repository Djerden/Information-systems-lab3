import {createBrowserRouter, RouterProvider} from "react-router-dom"
import Groups from "./pages/nav_menu/Groups.jsx";
import Root from "./components/Root.jsx";
import ErrorPage from "./pages/ErrorPage.jsx";
import AdminPanel from "./pages/nav_menu/AdminPanel.jsx";
import SignInPage from "./pages/auth/SignInPage.jsx";
import SignUpPage from "./pages/auth/SignUpPage.jsx";

function App() {

    const router = createBrowserRouter([
        {
            path: '/', RouterProvider,
            element: <Root />,
            errorElement: <ErrorPage/>,
            children: [
                {
                    path: '/groups',
                    element: <Groups />,
                },
                {
                  path: '/admin',
                  element: <AdminPanel/>
                },
                {
                    path: '/sign-in',
                    element: <SignInPage />
                },
                {
                    path: '/sign-up',
                    element: <SignUpPage />
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

