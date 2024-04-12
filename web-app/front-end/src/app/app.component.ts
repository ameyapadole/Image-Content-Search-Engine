import {Component, Inject} from '@angular/core';
import {FormControl} from '@angular/forms';
import {Observable} from 'rxjs';
import {map, startWith} from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatProgressBarModule, ProgressBarMode } from '@angular/material/progress-bar';
import { MatBottomSheet, MatBottomSheetRef } from '@angular/material/bottom-sheet';
import { MatListModule } from '@angular/material/list';
import {MAT_BOTTOM_SHEET_DATA} from '@angular/material/bottom-sheet';




@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  
  title = 'image-object-search-engine';
  myControl = new FormControl();
  options: string[] = [];
  filteredOptions: Observable<string[]> = new Observable();
  luceneToggle = false;
  query = '';
  imageList: Array<string> = [];
  imageListTest: Array<string> = [];
  baseUrl = 'http://10.13.180.247:5555/';
  postRequestData = '';
  value = 0;
  mode: ProgressBarMode = 'buffer';
  loadingText = 'Fetching using Lucene';
  resourcesLoading  = true;
  responseImageList: any = {};

  ngOnInit() {
    this.filteredOptions = this.myControl.valueChanges.pipe(
      startWith(''),
      map(value => this._filter(value)),
    );
    this.httpClient.get(this.baseUrl + "categories", {headers: {'Access-Control-Allow-Origin': '*'}})
    .subscribe(
      (val) => {
          console.log("POST call successful value returned in body", 
                      val);
                      let responseData: any = val;
                      this.options = responseData.categories.sort();
                      console.log(this.options);
      },
      response => {
          console.log("POST call in error", response);
          
      },
      () => {
          console.log("The POST observable is now completed.");
      });

  }

  constructor(private _snackBar: MatSnackBar, private httpClient: HttpClient, private progressBar: MatProgressBarModule, private _bottomSheet: MatBottomSheet, private matList: MatListModule) {}

  openBottomSheet(): void {
    let bottomSheetRef = this._bottomSheet.open(BottomSheetOverviewExampleSheet, {data: this.options});
    bottomSheetRef.afterDismissed().subscribe((dataFromChild) => {
      this.query = dataFromChild;
      this.search(dataFromChild);

  });
  }

  openSnackBar() {
    if(!this.luceneToggle)
    {
      this._snackBar.open("Switched to Lucene", "Dismiss");
    }
    else
    {
      {
        this._snackBar.open("Switched to Hadoop", "Dismiss");
      }
    }
  }

  changeMode()  {
    this.mode = 'query';
  }
  search(query: string) {
    this.resourcesLoading = true;
    this.imageList = [];
    this.mode = 'indeterminate';
    this.value = 15;
    console.log(query);
    let formData = new FormData();
    formData.append("query", query);
    formData.append("mode", this.luceneToggle ? "lucene" : "hadoop");
    this.httpClient.post(this.baseUrl + "search", formData, {headers: {'Access-Control-Allow-Origin': '*'}})
    .subscribe(
        (val) => {
            console.log("POST call successful value returned in body", 
                        val);
                        this.value = 25;
                        this.responseImageList = val;
                        let length = this.responseImageList.length;
                        let counter = 0;
                        // this.imageList = this.responseImageList['images'];
                        for(let element of this.responseImageList['images'])
                        {
                          counter++;
                          this.value = (75 * counter / length);
                          let newElement = "../assets/images/" + element;
                          this.imageList.push(newElement);
                        }
                        this.resourcesLoading = false;
        },
        response => {
            console.log("POST call in error", response);
            
        },
        () => {
            console.log("The POST observable is now completed.");
        });

  }

  sendPostRequest(data: any): Observable<any> {
    return this.httpClient.post<any>(this.baseUrl, data);
}

  private _filter(value: string): string[] {
    const filterValue = value.toLowerCase();

    return this.options.filter(option => option.toLowerCase().includes(filterValue));
  }
}


@Component({
  selector: 'bottomsheetoverviewexamplesheet',
  templateUrl: './bottom-sheet-categories-sheet.html',
})

export class BottomSheetOverviewExampleSheet {

  ngOnInit() {
  }
  constructor(private _bottomSheetRef: MatBottomSheetRef<BottomSheetOverviewExampleSheet>, @Inject(MAT_BOTTOM_SHEET_DATA) public data: {options: string[]}) {}
  
  options: any = this.data;
  sendMessage(query: any) {
    // this._bottomSheetRef.dismiss();
    this._bottomSheetRef.dismiss(query);
  }
}
