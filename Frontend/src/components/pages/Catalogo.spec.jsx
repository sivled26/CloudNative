import { fireEvent, render, screen } from "@testing-library/react"
import Catalogo from "./Catalogo"; 
import React from "react";
import Product from "../organisms/Product";
import Producto from "../organisms/Producto";

beforeEach(()=>{
    Storage.prototype.getItem = jest.fn(
        ()=> JSON.stringify([]))
    Storage.prototype.setItem = jest.fn()
})

describe('Catalogo component', ()=>{
    const mockProduct = {
        code: 1,
        image: "http://www.example.com/imagen1.png",
        name: "XBox one",
        description: "Consola xbox one 1tb de memoria y 1 control",
        category: "Consolas",
        price: "489.990 clp"
    }

    it('Renderiza el producto correctamente', () => {
        render(<Catalogo {...mockProduct}/>)
        expect(screen.getByText("XBox one")).toBeInTheDocument()
        expect(screen.getByText("Consola xbox one 1tb de memoria y 1 control")).toBeInTheDocument()
        expect(screen.getByText("489.990 clp")).toBeInTheDocument()
    })

        it('Se invoca al localStorage al clickear en botón', () => {
        render(<Producto {...mockProduct} />)
        const button = screen.getByText("Agregar al carro")
        fireEvent.click(button)
        expect(localStorage.setItem).toHaveBeenCalledWith(
            'products', JSON.stringify([mockProduct])
        )
    })
})