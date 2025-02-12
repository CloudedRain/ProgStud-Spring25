/** @OnlyCurrentDoc */

function GenerateData() {
  const rows = 100;
  const cols = 3;

  var spreadsheet = SpreadsheetApp.getActive();
  for (let i = 0; i < rows; i++) {
    for (let j = 0; j < cols; j++) {
      currentCol = String.fromCharCode("A".charCodeAt(0) + j)
      spreadsheet.getRange('' + currentCol + String.fromCharCode(i)).activate();
      spreadsheet.getCurrentCell().setValue('0');
    }
  }
};
