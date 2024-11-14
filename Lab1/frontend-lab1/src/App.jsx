import {createBrowserRouter, RouterProvider} from "react-router-dom"
import Groups from "./pages/Groups.jsx";
import Root from "./components/Root.jsx";
import ErrorPage from "./pages/ErrorPage.jsx";
import Home from "./pages/Home.jsx";
import AdminPanel from "./pages/AdminPanel.jsx";
import SignInPage from "./pages/SignInPage.jsx";
import SignUpPage from "./pages/SignUpPage.jsx";

function App() {

    const router = createBrowserRouter([
        {
            path: '/', RouterProvider,
            element: <Root />,
            errorElement: <ErrorPage/>,
            children: [
                {
                    path: '/',
                    element: <Home />
                },
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

