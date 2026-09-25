import React from 'react';
import { fireEvent, render, screen } from '@testing-library/react';
import Catalogo from './Catalogo';
import Producto from '../organisms/Producto';
import { productosApi } from '../../api/productosApi';

jest.mock('../../api/productosApi', () => ({ productosApi: { listar: jest.fn() } }));

beforeEach(() => {
  Storage.prototype.getItem = jest.fn(() => JSON.stringify([]));
  Storage.prototype.setItem = jest.fn();
});

describe('Catalogo', () => {
  test('renderiza los productos que entrega la API', async () => {
    productosApi.listar.mockResolvedValue([
      { id: 1, nombre: 'XBox one', descripcion: 'Consola xbox one 1tb', categoria: 'Consolas', precio: 489990, imagenUrl: 'x.png' },
    ]);
    render(<Catalogo />);
    expect(await screen.findByText('XBox one')).toBeInTheDocument();
    expect(screen.getByText('Consola xbox one 1tb')).toBeInTheDocument();
    expect(screen.getByText('$489.990')).toBeInTheDocument();
  });

  test('agregar al carro guarda el producto en localStorage', () => {
    const producto = { code: 1, image: 'x.png', name: 'XBox one', description: 'Consola', category: 'Consolas', price: '489.990' };
    render(<Producto {...producto} />);
    fireEvent.click(screen.getByText('Agregar al carro'));
    expect(localStorage.setItem).toHaveBeenCalledWith('products', JSON.stringify([producto]));
  });
});
