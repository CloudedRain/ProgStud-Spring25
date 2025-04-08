# Programing Studio Journal
Kjeld Nelson\
Spring 2025
## Monday 1/27/25
- [Plan A/B/C for Programing Studio](https://docs.google.com/document/d/1dj5Ut02pOiFnnO70w52i3QF_NHkqCML97zaG4LuyWFc/edit?usp=sharing)
- [Google Sheet for script testing](https://docs.google.com/spreadsheets/d/1Uxt-8pZ6vzg6PlAcBTOrV4rqpeXfcES8H2Hz8TCf1HY/edit?usp=sharing)
- [Sheets API](https://developers.google.com/sheets/api/reference/rest)
- [RCV](https://www.rcvresources.org/how-rcv-works)
## Timeline
**Week of 2/10** -  Study RCV algorithm and Sheets API ✔️\
**Weeks of 2/24-3/3** - Familiarize myself with JavaScript whist developing data generation macro ✔️\
**Week of 3/10** - Start writing RCV function, looping though all the data and performing actions ✔️\
**Week of 3/17** - Add RCV functionality, performing instant run off and outputting clear information\
**3/24-3/25 (2 classes)** - Design questionare to collect real-world data\
**3/26-3/27 (2 classes)** - Send out my survey to begin receiving responses\
**3/31-4/11 (7 classes)** - Polish JavaScript macro and add faulty-data handling\
**Week of 4/7** - Cut off data collection and start using my JavaScript macro\
**Future Additions** - Public data findings, package program into something others can use

## Monday 2/10/25
- [Applying Ranked Choice Voting](https://www.rankedvote.co/guides/applying-ranked-choice-voting/how-to-calculate-ranked-choice-voting-with-google-forms-and-google-sheets)
- I started writing a JavaScript macro to generate test data

## Monday 2/24/25
First day back from break.
```
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
```
A prototype loop for generating data. I'm currently working out a range error and figuring out JavaScript.\
--\
11:50 AM: Solved issues by normalizing my loops to 1 based indexing.
```
function GenerateData() {
  const rows = 100;
  const cols = 3;

  var spreadsheet = SpreadsheetApp.getActive();
  let startRow = 2;
  let startCol = 1;
  for (let i = startRow; i <= rows + startRow; i++) {
    for (let j = startCol-1; j < cols + startCol-1; j++) {
      let cell = String.fromCharCode("A".charCodeAt(0) + j).concat(i);
      spreadsheet.getRange(cell).activate();
      spreadsheet.getCurrentCell().setValue('0');
    }
  }
}; 
```
The macro is now able to automatically set the values of large chunks of the sheet.

## Tuesday 2/25/25
Found an example of a Durstenfeld shuffle algorithm for JavaScript on stack overflow.
```
function shuffleArray(array) {
    for (var i = array.length - 1; i >= 0; i--) {
        var j = Math.floor(Math.random() * (i + 1));
        var temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
```
Successfully implemented this arlgorithm to generate variable amounts of random data.
```
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
```
![Toy Data Sheet](https://github.com/CloudedRain/ProgStud-Spring25/blob/main/Journal%20Images/5x5-data-rcv.png)

## Friday 2/28/25
I met with Ms. Po and she really liked my project!\
She will be talking about it on a radio show featuring pojects at Baxter.\
Her request to me is clear data about the difference in result between ranked-choice and majority vote.\
I also would like to turn my JavaScript macro into some sort of template for easy replication and distribution.

## Monday 3/3/25
Today I emailed Ms. Po about RCV voting, providing her with one of my resources on it and giving a brief explanation in my own words.\
I hope that this will allow her to have a much clearer understanding of my project when she talks about it on Maine Calling.\
**10:50 AM** As a first step in the algorithm, I wrote a messy function to run though the data and count\
the number of 1st choices. This is pretty unoptimised, but progress nonetheless.
```
function rcvAlgorithm() {
  var spreadsheet = SpreadsheetApp.getActive();
  const rows = 100;
  const cols = 5;
  const startRow = 2;
  const startCol = 1;
  var votes = [];
  for (let i = 0; i < cols; i++) {
    votes[i] = 0;
  }
  var choice;

  for (let i = startRow; i <= rows + startRow + 1; i++) {
    for (let j = 0; j < cols; j++) {
      column = j + startCol-1;
      let cell = String.fromCharCode("A".charCodeAt(0) + column).concat(i);
      spreadsheet.getRange(cell).activate();
      if (i == rows + startRow + 1) {
        spreadsheet.getCurrentCell().setValue(votes[j]);
      }
      else {
        choice = spreadsheet.getCurrentCell().getValue();
        console.log(choice);
        if (choice == 1) {
          votes[j] ++;
          console.log(votes[j]);
        }
      }
    }
  }

  spreadsheet.getRange('F2').activate();
  spreadsheet.getCurrentCell().setValue();
};
```
![First Choice Results](https://github.com/CloudedRain/ProgStud-Spring25/blob/main/Journal%20Images/first-choice-results.png)

## Monday 3/10/25
I have optimised my functions so that they directly pull values from the spreadsheet instead of having to activate each box.
```
  for (let i = startCol; i < cols + startCol; i++) { 
    votes[i] = 0;
  }

  for (let i = startRow; i <= rows + startRow + 1; i++) {
    for (let j = startCol; j < cols + startCol; j++) {
      if (i == rows + startRow + 1) {
        spreadsheet.getRange(cellID(j, i)).setValue(votes[j]);
      }
      else {
        console.log(j + ", " + cellID(j, i));
        choice = spreadsheet.getRange(cellID(j, i)).getValue(); // Instant getter
        votes[j] += (choice == 1) ? 1 : 0;
      }
    }
  }
};
```
**To do:** Allow the program to detect the start row and column. Tally up non-first choices to apply RCV.
