<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<style>
input[type=text][type=number] {
  width: 100%;
  padding: 12px 20px;
  margin: 8px 0;
  display: inline-block;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
}

input[type=submit] {
  width: 100%;
  background-color: #4CAF50;
  color: white;
  padding: 14px 20px;
  margin: 8px 0;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

input[type=submit]:hover {
  background-color: #45a049;
}

div {
  border-radius: 5px;
  background-color: #f2f2f2;
  padding: 20px;
}
</style>

<body>
	<div align="center">
		<h2>Image grouping tool</h2>
		<form action="predict" method="post" enctype="multipart/form-data">
			<label for="threshold">Threshold</label> 
			<input type="number" id="threshold" name="threshold" min="0" max="100" />
			<br /> 
			<label for="files">Select images:</label>
			<br /> 
			<input type="file" name="files" id="images" size="50" multiple /> <br />
			<br /> 
			<input type="submit" value="Upload Images" />
		</form>
		<p style="font-size:10px"><i>Created By Damian Samek</i></p>
	</div>

</body>
</html>
