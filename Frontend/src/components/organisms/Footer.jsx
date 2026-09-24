import React from 'react'

export default function Footer() {
  return (

    <footer className="footer">
        <div className="footer-content">
            <div className="footer-logo">
                <img src="https://raw.githubusercontent.com/0dr4d3kdb/proyectofullstack1/a998f004585f2377fc7ea88e2dae116bd8ee0fb2/Level-Up.png" alt="Level-Up" height="40"/>
                <span>Level-UP Gamer</span>
            </div>
            <div className="footer-social">
                <a href="https://www.instagram.com/santiago_delvis/" title="Instagram"><img src="https://cdn-icons-png.flaticon.com/512/2111/2111463.png" alt="Instagram" height="28"/></a>
                <a href="https://x.com/Sivled3" title="Twitter"><img src="https://cdn-icons-png.flaticon.com/512/733/733579.png" alt="Twitter" height="28"/></a>
                <a href="#" title="Facebook"><img src="https://cdn-icons-png.flaticon.com/512/733/733547.png" alt="Facebook" height="28"/></a>
            </div>
        </div>
        <div className="footer-copy">
            &copy; 2025 Level-UP Gamer. Todos los derechos reservados.
        </div>
    </footer>
  )
}
