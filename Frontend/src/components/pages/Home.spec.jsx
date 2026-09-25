import React from 'react';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import Home from './Home';
import { productosApi } from '../../api/productosApi';

jest.mock('../../api/productosApi', () => ({ productosApi: { listar: jest.fn() } }));

describe('Home', () => {
  test('muestra los productos destacados obtenidos desde la API', async () => {
    productosApi.listar.mockResolvedValue([
      { id: 1, nombre: 'Mouse Logitech', descripcion: 'Mouse gamer', precio: 29990, imagenUrl: 'a.jpg' },
      { id: 2, nombre: 'Silla Gamer', descripcion: 'Silla ergonómica', precio: 159990, imagenUrl: 'b.jpg' },
    ]);
    render(<MemoryRouter><Home /></MemoryRouter>);
    expect(await screen.findByText('Mouse Logitech')).toBeInTheDocument();
    expect(screen.getByText('Silla Gamer')).toBeInTheDocument();
    expect(screen.getByRole('main')).toBeInTheDocument();
  });

  test('muestra el mensaje de error si la API falla', async () => {
    productosApi.listar.mockRejectedValue({ userMessage: 'No se pudo conectar con el servidor.' });
    render(<MemoryRouter><Home /></MemoryRouter>);
    expect(await screen.findByText('No se pudo conectar con el servidor.')).toBeInTheDocument();
  });
});
