import React from 'react';
import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import Home from './Home';

describe('Home Component', () => {
  const renderHome = () => {
    return render(
      <BrowserRouter>
        <Home />
      </BrowserRouter>
    );
  };

  test('renders main product section', () => {
    renderHome();
    const mainElement = screen.getByRole('main');
    expect(mainElement).toBeDefined();
  });

  test('shows product titles', () => {
    renderHome();
    expect(screen.getByText('Teclado Mecánico RGB')).toBeDefined();
    expect(screen.getByText('Mouse Gamer Ergonómico')).toBeDefined();
    expect(screen.getByText('Audífonos con Micrófono')).toBeDefined();
  });

  test('displays buy buttons', () => {
    renderHome();
    const buyButtons = screen.getAllByText('Comprar');
    expect(buyButtons.length).toBeGreaterThan(0);
  });
});
