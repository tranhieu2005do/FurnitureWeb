import { Name } from "./named.js";
export function Person(props) {
  return (
    <>
      <h1>Information</h1>
      <Name fname={props.fname} lname={props.lname} />
      <h2>Age: {props.age}</h2>
      <h2>Address: {props.address}</h2>
    </>
  );
}
