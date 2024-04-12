import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input'
import { MatFormFieldModule} from '@angular/material/form-field';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { AppComponent, BottomSheetOverviewExampleSheet } from './app.component';
import {MatBottomSheetRef} from '@angular/material/bottom-sheet';
import { HttpClientModule } from '@angular/common/http';
import {MatBottomSheetModule} from '@angular/material/bottom-sheet';
import { MatListModule } from '@angular/material/list';
import {MatButtonModule} from '@angular/material/button';;



@NgModule({
  declarations: [
    AppComponent,
    BottomSheetOverviewExampleSheet
  ],
  imports: [
    BrowserModule, 
    MatSlideToggleModule,
    MatAutocompleteModule,
    MatFormFieldModule,
    FormsModule, 
    ReactiveFormsModule,
    MatInputModule,
    BrowserAnimationsModule,
    MatSnackBarModule,
    MatProgressBarModule,
    MatBottomSheetModule,
    MatListModule,
    MatButtonModule,
    HttpClientModule
  ],
  exports: [
    MatSlideToggleModule
  ],
  
  providers: [{ provide: MatBottomSheetRef, useValue: {} },],
  bootstrap: [AppComponent]
})
export class AppModule { }
