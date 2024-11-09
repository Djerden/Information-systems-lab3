import {createBrowserRouter, RouterProvider} from "react-router-dom"
import Groups from "./pages/Groups.jsx";
import Root from "./components/Root.jsx";
import Authentication from "./pages/Authentication.jsx";
import ErrorPage from "./pages/ErrorPage.jsx";
import Home from "./pages/Home.jsx";
import AdminPanel from "./pages/AdminPanel.jsx";

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
                    path: '/auth',
                    element: <Authentication />
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

