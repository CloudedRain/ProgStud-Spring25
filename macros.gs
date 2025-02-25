/** @OnlyCurrentDoc */

function GenerateData() {
  var spreadsheet = SpreadsheetApp.getActive();
  const rows = 100;
  const cols = 3;
  const startRow = 2;
  const startCol = 1;
  var data = [];

  for (let i = 1; i <= cols; i++) {
    data.push(i);
  }

  for (let i = startRow; i <= rows + startRow; i++) {
    shuffleArray(data);
    for (let j = 0; j < cols; j++) {
      column = j + startCol-1;
      let cell = String.fromCharCode("A".charCodeAt(0) + column).concat(i);
      spreadsheet.getRange(cell).activate();
      spreadsheet.getCurrentCell().setValue(data[j]);
    }
  }
}; 

/* Randomize array in-place using Durstenfeld shuffle algorithm */
function shuffleArray(array) {
    for (var i = array.length - 1; i >= 0; i--) {
        var j = Math.floor(Math.random() * (i + 1));
        var temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
