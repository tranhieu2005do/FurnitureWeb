import demoPic from "../../assets/img/giang.jpg"
import "./Header.css"

function Header(){
  return(
    <>
    <header>
      <img src={demoPic} alt=""></img>
      <h1>Welcome to my Funiture Shop</h1>
    </header>
    </>
  );
}


export default Header;