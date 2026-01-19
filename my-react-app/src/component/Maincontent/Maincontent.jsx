import PropTypes from "prop-types"
import "./Maincontent.css"

function Maincontent({image, title, desc}){
  return(
    <li>
      <img src={image} alt = {title}></img>
      <h2>{title}</h2>
      <p>{desc}</p>
    </li>
  );
}

Maincontent.propTypes = {
  image: PropTypes.string.isRequired,
  title: PropTypes.string.isRequired,
  desc: PropTypes.string
}

export default Maincontent;