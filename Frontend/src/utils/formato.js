// Formatea un precio en pesos chilenos sin romper si viene null o como texto.
export function formatearPrecio(precio) {
  const numero = Number(precio);
  if (precio === null || precio === undefined || Number.isNaN(numero)) return '-';
  return numero.toLocaleString('es-CL');
}
