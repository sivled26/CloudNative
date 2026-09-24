import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import Home from './components/pages/Home'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import Header from './components/organisms/Header'
import Login from './components/pages/Login'
import Registro from './components/pages/Registro'
import PerfilPage from './components/pages/PerfilPage'
import CarritoPage from './components/pages/CarritoPage'
import Catalogo from './components/pages/Catalogo'
import AboutUs from './components/pages/AboutUs'
import Blog from './components/pages/Blog'
import Resena from './components/pages/Resena'
import Footer from './components/organisms/Footer'
import DashboardAdmin from './components/pages/DashboardAdmin'

function App() {
  const [count, setCount] = useState(0)

  return (
    <>
   
      <BrowserRouter>
       <Header/>
      {/* <Link to='/'>Home</Link>
      <Link to='/about'>About</Link> */}
      <Routes>
        <Route path='/' element={<Home/>}/>
        <Route path='/login' element={<Login/>}/>
        <Route path='/registro' element={<Registro/>}/>
        <Route path='/perfil' element={<PerfilPage/>}/>
        <Route path='/carrito' element={<CarritoPage/>}/>
        <Route path='/catalogo' element={<Catalogo/>}/>
        <Route path='/aboutus' element={<AboutUs/>}/>
        <Route path='/blog' element={<Blog/>}/>
        <Route path='/resena' element={<Resena/>}/>
        <Route path="/admin" element={<DashboardAdmin />} />
      </Routes>
    </BrowserRouter>
    <Footer/>
    </>
  )
}

export default App
